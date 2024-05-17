package com.example.bill

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.bill.InvoiceActivity.Item
import com.example.bill.com.example.bill.ItemAdapter


class InvoiceDisplayActivity : AppCompatActivity() {
    private lateinit var billDateTextView: TextView
    private lateinit var billNoTextView: TextView
    private lateinit var totalAmountTextView: TextView
    private lateinit var gstAmountTextView: TextView
    private lateinit var grandTotalTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var customernameheading : TextView
    private lateinit var CustomerAddressHeading : TextView
    private lateinit var adapter: ItemAdapter // Assuming you have an adapter for the RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_display)

        initializeViews()

        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val billDate = intent.getStringExtra("billDate") ?: return
        val billNo = intent.getStringExtra("billNo") ?: return
        val customerName = intent.getStringExtra("customerName") ?: return
        val customerAddress = intent.getStringExtra("customerAddress") ?: return
        val totalAmount = intent.getDoubleExtra("totalAmount", 0.0) ?: return
        val gstAmount = intent.getDoubleExtra("gstAmount", 0.0) ?: return
        val grandTotal = intent.getDoubleExtra("grandTotal", 0.0) ?: return
        val itemList: List<Item> = intent.getSerializableExtra("itemList") as? List<Item> ?: emptyList() ?: return


        val billDetails = hashMapOf(
            "billDate" to billDate,
            "billNo" to billNo,
            "customerName" to customerName,
            "customerAddress" to customerAddress,
            "totalAmount" to totalAmount,
            "gstAmount" to gstAmount,
            "grandTotal" to grandTotal
        )

        firestore.collection("bills")
            .add(billDetails)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Bill added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding bill", e)
            }

        // Set data to TextViews
        billDateTextView.text = "Date: $billDate"
        billNoTextView.text = "Bill No.: $billNo"
        customernameheading.text = "Customer Name: $customerName"
        CustomerAddressHeading.text = "Customer Address: $customerAddress"
        totalAmountTextView.text = "Total Amount: $totalAmount"
        gstAmountTextView.text = "GST Amount: $gstAmount"
        grandTotalTextView.text = "Grand Total: $grandTotal"

        // Initialize and set up RecyclerView adapter
        adapter = ItemAdapter(itemList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun initializeViews() {
        billDateTextView = findViewById(R.id.billDateTextView) ?: run {
            Log.e("InvoiceDisplayActivity", "billDateTextView not found")
            throw IllegalStateException("billDateTextView not found")
        }
        billNoTextView = findViewById(R.id.billNoTextView) ?: run {
            Log.e("InvoiceDisplayActivity", "billNoTextView not found")
            throw IllegalStateException("billNoTextView not found")
        }
        customernameheading = findViewById(R.id.customernameheading) ?: run {
            Log.e("InvoiceDisplayActivity", "billNoTextView not found")
            throw IllegalStateException("billNoTextView not found")
        }
        CustomerAddressHeading = findViewById(R.id.customeraddressheading) ?: run {
            Log.e("InvoiceDisplayActivity", "billNoTextView not found")
            throw IllegalStateException("billNoTextView not found")
        }
        totalAmountTextView = findViewById(R.id.totalAmountTextView) ?: run {
            Log.e("InvoiceDisplayActivity", "totalAmountTextView not found")
            throw IllegalStateException("totalAmountTextView not found")
        }
        gstAmountTextView = findViewById(R.id.gstAmountTextView) ?: run {
            Log.e("InvoiceDisplayActivity", "gstAmountTextView not found")
            throw IllegalStateException("gstAmountTextView not found")
        }
        grandTotalTextView = findViewById(R.id.grandTotalTextView) ?: run {
            Log.e("InvoiceDisplayActivity", "grandTotalTextView not found")
            throw IllegalStateException("grandTotalTextView not found")
        }
        recyclerView = findViewById(R.id.rvitemdetail) ?: run {
            Log.e("InvoiceDisplayActivity", "recyclerView not found")
            throw IllegalStateException("recyclerView not found")
        }
    }
}