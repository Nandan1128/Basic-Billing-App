package com.example.bill

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.bill.R.id.cardadd
import com.google.firebase.firestore.FirebaseFirestore

class add_customer_activity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var cardadd : CardView
    private lateinit var db: FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_customer)

        nameEditText = findViewById(R.id.nameEditText)
        addressEditText = findViewById(R.id.addressEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        cardadd = findViewById(R.id.cardadd)

        db = FirebaseFirestore.getInstance()
        cardadd.setOnClickListener {
            addCustomer()
        }


    }

    fun addCustomer() {
        val name = nameEditText.text.toString()
        val address = addressEditText.text.toString()
        val phone = phoneEditText.text.toString()

        if (name.isNotEmpty() && address.isNotEmpty() && phone.isNotEmpty()) {
            val customer = Customer(name, address, phone)

            db.collection("Customers")
                .add(customer)
                .addOnSuccessListener {
                    Toast.makeText(this, "Customer added successfully", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity after adding the customer
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding customer", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error adding customer", e)
                }
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

}