package com.example.bill.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bill.R
import com.example.bill.adaptor.itemviewAdaptor
import com.example.bill.model.Items
import com.google.firebase.firestore.FirebaseFirestore

// Inherit from AppCompatActivity
class viewitemActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var itemList: MutableList<Items>
    private lateinit var adapter: itemviewAdaptor

    // Override the onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the layout file for this activity
        // Make sure you have a layout file named 'activity_viewitem.xml' or similar
        setContentView(R.layout.view_item)

        // Now you can initialize your views and other components
        db = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.rvitemview) // Replace with the actual ID from your XML
        recyclerView.layoutManager = LinearLayoutManager(this)
        itemList = mutableListOf()
        adapter = itemviewAdaptor(itemList)
        recyclerView.adapter = adapter

        loadItems()

        // Fetch data from Firestore and populate)
        // ... rest of your initialization logic for the adapter, list, etc.
    }

    private fun loadItems() {
        db.collection("Items")
            .get()
            .addOnSuccessListener { querySnapshots ->
                itemList.clear()
                for (document in querySnapshots) {
                    val item = document.toObject(Items::class.java)
                    itemList.add(item)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this,"Error Loading items", Toast.LENGTH_SHORT).show()
            }
    }
}