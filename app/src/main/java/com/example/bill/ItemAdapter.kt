package com.example.bill.com.example.bill

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bill.InvoiceActivity.Item
import com.example.bill.R

class ItemAdapter(private val itemList: List<Item>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bill_item_detail, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemNameTextView: TextView = itemView.findViewById(R.id.listitemname)
        private val itemPriceTextView: TextView = itemView.findViewById(R.id.listitemquntity)
        private val itemQuantityTextView: TextView = itemView.findViewById(R.id.listitemprice)
        private val itemTotalTextView: TextView = itemView.findViewById(R.id.listitemtotal)

        fun bind(item: Item) {
            Log.d("itemname",item.name)
            itemNameTextView.text = "sugar"
            itemPriceTextView.text = "${item.price}"
            itemQuantityTextView.text = "${item.quantity}"

            val total = item.price * item.quantity
            itemTotalTextView.text = "$total"

        }
    }
}
