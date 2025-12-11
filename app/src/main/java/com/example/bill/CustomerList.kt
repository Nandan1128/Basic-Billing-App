package com.example.bill

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bill.adaptor.CustomerAdapter
import com.example.bill.model.Customer
import com.google.firebase.firestore.FirebaseFirestore

class CustomerList : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var customerList: MutableList<Customer>
    private lateinit var adapter: CustomerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = FirebaseFirestore.getInstance()
        customerList = mutableListOf()
        adapter = CustomerAdapter(customerList)

        recyclerView.adapter = adapter

        loadCustomers()
    }

    private fun loadCustomers() {
        db.collection("Customers")
            .get()
            .addOnSuccessListener { querySnapshot ->
                customerList.clear()
                for (document in querySnapshot) {
                    val customer = document.toObject(Customer::class.java)
                    customerList.add(customer)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading customers", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error loading customers", e)
            }
    }
}
