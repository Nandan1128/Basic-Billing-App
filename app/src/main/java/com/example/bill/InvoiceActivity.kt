package com.example.bill

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InvoiceActivity : AppCompatActivity() {
    private lateinit var customerNameAutoComplete: AutoCompleteTextView
    private lateinit var customerAddressEditText: EditText
    private lateinit var addItemButton: Button
    private lateinit var createInvoiceButton: Button
    private lateinit var itemDetailsLayout: LinearLayout
    private lateinit var totalAmountTextView: TextView
    private lateinit var gstTextView: TextView
    private lateinit var grandTotalTextView: TextView
    private lateinit var gstSpinner: Spinner
    private lateinit var calbtn : Button
    private var itemList: MutableList<Item> = mutableListOf()
    private var totalAmount: Double = 0.0
    private var gstAmount: Double = 0.0
    private var grandTotal: Double = 0.0
    private val gstList = listOf("5%", "12%", "18%", "28%") // List of valid GST percentages
    private val customerNames = mutableListOf<String>()

    private val customerAddresses = mutableListOf<String>()

    private var itemDetailsList: ArrayList<ItemDetails> = ArrayList()


    private lateinit var firestore: FirebaseFirestore
    private lateinit var date : TextView
    private lateinit var sharedPreferences: SharedPreferences
    private val BILL_COUNTER_KEY = "bill_counter"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)

        firestore = FirebaseFirestore.getInstance()
        customerNameAutoComplete = findViewById(R.id.customerNameAutoComplete)
        customerAddressEditText = findViewById(R.id.customerAddressEditText)
        addItemButton = findViewById(R.id.addItemButton)
        createInvoiceButton = findViewById(R.id.createInvoiceButton)
        calbtn = findViewById(R.id.calbtn)
        itemDetailsLayout = findViewById(R.id.itemDetailsLayout)
        totalAmountTextView = findViewById(R.id.totalAmountTextView)
        gstTextView = findViewById(R.id.gstTextView)
        grandTotalTextView = findViewById(R.id.grandTotalTextView)
        gstSpinner = findViewById(R.id.gstSpinner)

        date = findViewById(R.id.date)
        date.text = getCurrentDateTime()

        sharedPreferences = getSharedPreferences("BillCounter", Context.MODE_PRIVATE)


        val billNumberTextView = findViewById<TextView>(R.id.billnum)
        billNumberTextView.text = generateBillNumber()

        addItemButton.setOnClickListener {
            addItemLayout()
        }
        calbtn.setOnClickListener {
            createInvoice()
        }

        createInvoiceButton.setOnClickListener {
            Log.d("button clicked","createinvoicebtn clicked")

            val customerName = customerNameAutoComplete.text.toString()
            val customerAddress = customerAddressEditText.text.toString()
            val intent = Intent(this,InvoiceDisplayActivity::class.java).apply {
                putExtra("billDate", getCurrentDateTime()) // Assuming you want to pass the current date as billDate
                putExtra("billNo", generateBillNumber())
                putExtra("customerName", customerName)
                putExtra("customerAddress", customerAddress)
                putExtra("totalAmount", totalAmount)
                putExtra("gstAmount", gstAmount)
                putExtra("grandTotal", grandTotal)
                putExtra("itemList", ArrayList(itemList)) // Convert MutableList to ArrayList if needed
            }
            startActivity(intent)
        }

        fetchCustomerNames()

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, customerNames)
        customerNameAutoComplete.setAdapter(adapter)

        customerNameAutoComplete.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedCustomerAddress = customerAddresses[position]
            customerAddressEditText.setText(selectedCustomerAddress)
        }

        // Set up GST Spinner
        val gstAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gstList)
        gstAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gstSpinner.adapter = gstAdapter
    }



    private fun validateGST(gst: String): Boolean {
        return gstList.contains(gst)
    }

    private fun fetchCustomerNames() {
        firestore.collection("Customers")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val customerName = document.getString("name")
                    val customerAddress = document.getString("address")
                    customerName?.let {
                        customerNames.add(it)
                    }
                    customerAddress?.let {
                        customerAddresses.add(it)
                    }
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, customerNames)
                customerNameAutoComplete.setAdapter(adapter)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }




    private fun addItemLayout() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemLayout = inflater.inflate(R.layout.item_layout, null)

        val itemNameAutoComplete = itemLayout.findViewById<AutoCompleteTextView>(R.id.itemNameAutoComplete)
        val itemPriceEditText = itemLayout.findViewById<EditText>(R.id.itemPriceEditText)
        val itemQuantityEditText = itemLayout.findViewById<EditText>(R.id.itemQuantityEditText)
        val itemTotalEditText = itemLayout.findViewById<EditText>(R.id.itemTotalEditText)

        fetchItemNamesAndPrices(itemNameAutoComplete, itemPriceEditText, itemQuantityEditText)

        itemQuantityEditText?.addTextChangedListener { editable ->
            val quantity1 = editable.toString().toIntOrNull() ?: 0
            val price1 = itemPriceEditText.text.toString().toDoubleOrNull() ?: 0.0
            val total1 = quantity1 * price1
            itemTotalEditText.setText(total1.toString()) // Convert total to String before setting
        }

        itemDetailsLayout.addView(itemLayout)
    }

    private fun fetchItemNamesAndPrices(
        itemNameAutoComplete: AutoCompleteTextView?,
        itemPriceEditText: EditText?,
        itemQuantityEditText: EditText?
    ) {
        // Ensure non-null references
        itemNameAutoComplete ?: return
        itemPriceEditText ?: return
        itemQuantityEditText ?: return

        itemList = mutableListOf() // Initialize itemList here

        firestore.collection("Items")
            .get()
            .addOnSuccessListener { documents ->
                itemDetailsList.clear() // Clear the list before adding items
                for (document in documents) {
                    val name = document.getString("name")
                    val price = document.getDouble("price")

                    name?.let { itemName ->
                        price?.let { itemPrice ->
                            itemDetailsList.add(ItemDetails(itemName, itemPrice))
                        }
                    }
                }

                val itemNames = itemDetailsList.map { it.name }
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, itemNames)
                itemNameAutoComplete.setAdapter(adapter)

                itemNameAutoComplete.setOnItemClickListener { _, _, position, _ ->
                    val selectedName = itemNames[position]
                    val selectedPrice = itemDetailsList.find { it.name == selectedName }?.price ?: 0.0
                    itemPriceEditText.setText(selectedPrice.toString())

                    // Set item quantity listener
                    itemQuantityEditText?.addTextChangedListener { editable ->
                        // Update item list with quantity
                        val quantity = editable.toString().toIntOrNull() ?: 0
                        val existingItem = itemList.find { it.name == selectedName }

                        if (existingItem != null) {
                            // Update existing item quantity
                            existingItem.quantity = quantity
                        } else {
                            // Add new item to the list
                            val newItem = Item(selectedName, selectedPrice, quantity)
                            itemList.add(newItem)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }
    private fun createInvoice() {
        val customerName = customerNameAutoComplete.text.toString()
        val customerAddress = customerAddressEditText.text.toString()
        val gstText = gstSpinner.selectedItem.toString() // Get selected GST from Spinner

        if (customerName.isNotEmpty() && customerAddress.isNotEmpty() && itemList.isNotEmpty() && validateGST(gstText)) {
            calculateInvoice()

        } else {
            Toast.makeText(this, "Please fill in all fields correctly and add items to create an invoice.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentDateTime() : String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        return currentDate
    }

    private fun calculateInvoice() {
        totalAmount = 0.0
        itemList.forEach { item ->
            val price = item.price
            val quantity = item.quantity
            totalAmount += price.toInt() * quantity
        }

        val gstPercentage = gstSpinner.selectedItem.toString().replace("%", "").toDouble() / 100
        gstAmount = totalAmount * gstPercentage
        grandTotal = totalAmount + gstAmount

        totalAmountTextView.text = String.format(Locale.getDefault(), "Total Amount: %.2f", totalAmount)
        gstTextView.text = String.format(Locale.getDefault(), "GST (%s): %.2f", gstSpinner.selectedItem, gstAmount)
        grandTotalTextView.text = String.format(Locale.getDefault(), "Grand Total: %.2f", grandTotal)
    }
    private fun generateBillNumber(): String {

        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val currentDate = sdf.format(Date())

        // Increment and store the bill counter
        val billCounter = sharedPreferences.getInt(BILL_COUNTER_KEY, 0) + 1
        sharedPreferences.edit().putInt(BILL_COUNTER_KEY, billCounter).apply()

        return "$currentDate - $billCounter"
    }

    data class Item(val name: String, val price: Double, var quantity: Int) : Serializable
    data class ItemDetails(val name: String, val price: Double)
}
