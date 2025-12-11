package com.example.bill

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.bill.ui.InvoiceActivity.Item
import com.example.bill.adaptor.ItemAdapter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.print.PrintAttributes
import android.print.PrintManager
import android.print.pdf.PrintedPdfDocument
import android.print.PrintDocumentAdapter
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintDocumentInfo
import android.view.View
import android.widget.Button
import java.io.FileOutputStream


class InvoiceDisplayActivity : AppCompatActivity() {
    private lateinit var billDateTextView: TextView
    private lateinit var billNoTextView: TextView
    private lateinit var totalAmountTextView: TextView
    private lateinit var gstAmountTextView: TextView
    private lateinit var rootLayout: View
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
        val rootLayout = findViewById<View>(R.id.rootLayout)


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

        findViewById<Button>(R.id.btnPrint).setOnClickListener {
            expandRecyclerViewFully(recyclerView)

            rootLayout.postDelayed({
                printInvoice()
            }, 200) // small delay ensures layout updated
        }

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
    private var printAttributes: PrintAttributes? = null

    private fun printInvoice() {
        val printManager = getSystemService(PRINT_SERVICE) as PrintManager

        printManager.print("Invoice_Print", object : PrintDocumentAdapter() {

            private var bitmap: Bitmap? = null

            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                // Save attributes for use in onWrite()
                printAttributes = newAttributes

                bitmap = getBitmapFromView(findViewById(R.id.rootLayout))

                if (bitmap == null) {
                    callback?.onLayoutFailed("Failed to render invoice view.")
                    return
                }

                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }

                val info = PrintDocumentInfo.Builder("invoice_bill.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()

                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<PageRange>,
                destination: ParcelFileDescriptor,
                cancellationSignal: CancellationSignal,
                callback: WriteResultCallback
            ) {
                val attrs = printAttributes
                if (attrs == null) {
                    callback.onWriteFailed("Print attributes are null")
                    return
                }

                val document = PrintedPdfDocument(this@InvoiceDisplayActivity, attrs)

                val page = document.startPage(0)
                val canvas = page.canvas

                bitmap?.let { bmp ->
                    val scale = canvas.width.toFloat() / bmp.width
                    val scaledHeight = (bmp.height * scale).toInt()
                    val scaledBitmap = Bitmap.createScaledBitmap(bmp, canvas.width, scaledHeight, true)
                    canvas.drawBitmap(scaledBitmap, 0f, 0f, null)
                }

                document.finishPage(page)

                try {
                    document.writeTo(FileOutputStream(destination.fileDescriptor))
                } catch (e: Exception) {
                    callback.onWriteFailed(e.message)
                    return
                } finally {
                    document.close()
                }

                callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            }

        }, null)
    }

    private fun getBitmapFromView(view: android.view.View): Bitmap {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return bitmap
    }

    private fun expandRecyclerViewFully(recyclerView: RecyclerView) {
        val adapter = recyclerView.adapter ?: return
        var totalHeight = 0

        for (i in 0 until adapter.itemCount) {
            val holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i))
            adapter.onBindViewHolder(holder, i)

            holder.itemView.measure(
                View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.UNSPECIFIED
            )

            totalHeight += holder.itemView.measuredHeight
        }

        recyclerView.layoutParams.height = totalHeight
        recyclerView.requestLayout()
    }


}