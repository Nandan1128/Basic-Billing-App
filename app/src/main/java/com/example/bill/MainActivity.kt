package com.example.bill

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    private lateinit var addbtn : CardView
    private lateinit var viewbtn : CardView
    private lateinit var viewitem : CardView
    private lateinit var createInvoicebtn : CardView
    private lateinit var addItemBtn : CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addbtn = findViewById(R.id.addcustomerbutton)
        viewbtn = findViewById(R.id.viewbtn)
        viewitem = findViewById(R.id.viewitem)
        createInvoicebtn = findViewById(R.id.createInvoiceBtn)
        addItemBtn = findViewById(R.id.additemBtn)

        addbtn.setOnClickListener {
            val intent = Intent(this, add_customer_activity::class.java)
            startActivity(intent)
        }
        viewbtn.setOnClickListener {
            val intent = Intent(this,CustomerList::class.java)
            startActivity(intent)
        }
        createInvoicebtn.setOnClickListener {
            val intent = Intent(this,InvoiceActivity::class.java)
            startActivity(intent)
        }
        addItemBtn.setOnClickListener {
            val intent = Intent(this,AddItemActivity::class.java)
            startActivity(intent)
        }
        viewitem.setOnClickListener {
            val intent = Intent(this,viewitemActivity::class.java)
            startActivity(intent)
        }


    }
}