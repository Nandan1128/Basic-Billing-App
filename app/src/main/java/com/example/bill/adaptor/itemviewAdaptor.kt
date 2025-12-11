package com.example.bill.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bill.R
import com.example.bill.model.Items

class itemviewAdaptor(private val itemList: List<Items>) :
    RecyclerView.Adapter<itemviewAdaptor.itemviewViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): itemviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemviewdisplay, parent, false)
        return itemviewViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: itemviewViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemnameTextView.text = item.name
        holder.priceTextView.text = item.price.toString()
    }

    class itemviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemnameTextView: TextView = itemView.findViewById(R.id.itemnameTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
    }

}