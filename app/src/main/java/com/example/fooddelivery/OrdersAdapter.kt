package com.example.fooddelivery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrdersAdapter(private val orders: List<OrderInfo>) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.emailTextView.text = "Email: ${order.emailUser}"
        holder.dateTextView.text = "Дата доставки: ${order.deliveryDateTime.toDate()}"
        holder.priceTextView.text = "Общая стоимость: ${order.totalPrice}"
        holder.statusTextView.text = if (order.delivered) "Доставлено" else "В процессе"
    }

    override fun getItemCount(): Int = orders.size

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emailTextView: TextView = itemView.findViewById(R.id.textViewEmail)
        val dateTextView: TextView = itemView.findViewById(R.id.textViewDate)
        val priceTextView: TextView = itemView.findViewById(R.id.textViewPrice)
        val statusTextView: TextView = itemView.findViewById(R.id.textViewStatus)
    }
}
