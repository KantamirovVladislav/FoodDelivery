package com.example.fooddelivery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class CartItemAdapter(private val dataSet: Map<FoodItem, Int>,
                      private val onIncrease: (FoodItem) -> Unit,
                      private val onDecrease: (FoodItem) -> Unit,
                      private val onRemove: (FoodItem) -> Unit):RecyclerView.Adapter<CartItemAdapter.ViewHolder>() {

    private val keys = dataSet.keys.toList()

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById(R.id.textViewForNameFood)
        val price: TextView = view.findViewById(R.id.textViewForPriceFood)
        val image: ImageView = view.findViewById(R.id.imageFood)
        val quantity: TextView = view.findViewById(R.id.textViewQuantity)
        val increaseButton: TextView = view.findViewById(R.id.buttonIncrease)
        val decreaseButton: TextView = view.findViewById(R.id.buttonDecrease)
        val removeButton: TextView = view.findViewById(R.id.buttonRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val foodItem = keys[position]
        val quantity = dataSet[foodItem] ?: 0

        holder.name.text = foodItem.name
        holder.image.load(foodItem.imageLink) {
            crossfade(true)
            placeholder(R.drawable.base_pizza_photo)
            error(R.drawable.base_pizza_photo)
        }
        holder.price.text = foodItem.price.toString()
        holder.quantity.text = quantity.toString()

        holder.increaseButton.setOnClickListener { onIncrease(foodItem) }
        holder.decreaseButton.setOnClickListener { onDecrease(foodItem) }
        holder.removeButton.setOnClickListener { onRemove(foodItem) }
    }

}