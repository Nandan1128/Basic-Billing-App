package com.example.bill.ui

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.bill.R
import com.example.bill.model.Items
import com.google.firebase.firestore.FirebaseFirestore

class AddItemActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var additem : CardView
    private lateinit var Firestore : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        nameEditText = findViewById(R.id.itemnameEditText)
        priceEditText = findViewById(R.id.priceEditText)
        additem = findViewById(R.id.additem)
        Firestore = FirebaseFirestore.getInstance()

        additem.setOnClickListener {
            addItem()
        }

    }

    fun addItem() {
        val name = nameEditText.text.toString()
        val priceStr = priceEditText.text.toString()

        if (name.isNotEmpty() && priceStr.isNotEmpty()) {
            val name = name.toString()
            val price = priceStr.toDouble()
            val item = Items(name, price)

            Firestore.collection("Items").add(item).addOnSuccessListener {
                Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error adding item", Toast.LENGTH_SHORT).show()
                Log.e(ContentValues.TAG, "Error adding customer", e)
            }

//            val intent = Intent()
////            intent.putExtra("item", item)
//            setResult(Activity.RESULT_OK, intent)
//            finish()
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }
}
