package com.example.fooddelivery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class FoodListAdapter(val dataSet: List<FoodItem>, private val onItemClick: (FoodItem) -> Unit) :
    RecyclerView.Adapter<FoodListAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textViewForNameFood)
        val price: TextView = view.findViewById(R.id.textViewForPriceFood)
        val image: ImageView = view.findViewById(R.id.imageFood)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(dataSet[position])
                }
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.food_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = dataSet[position].name
        holder.price.text = dataSet[position].price.toString() + " ₽"
        holder.image.load(dataSet[position].imageLink) {
            crossfade(true)
            placeholder(R.drawable.base_pizza_photo)
            error(R.drawable.base_pizza_photo)
        }
    }
}