package com.example.fooddelivery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FoodList : Fragment() {

    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_food_list, container, false)
        val db = Firebase.firestore

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(activity, 2)

        val foodList = mutableListOf<FoodItem>()

        lifecycleScope.launch {
            db.collection("foodList").get().await().let { querySnapshot ->
                querySnapshot.documents.forEach { document ->
                    val food = document.toObject<FoodItem>()
                    if (food != null) {
                        foodList.add(food)
                    }
                }
            }

            recyclerView.adapter = FoodListAdapter(foodList) { foodItem ->
                CartManager.addItem(foodItem)
                showAddedToCartMessage(foodItem)
            }
        }

        view.findViewById<Button>(R.id.buttonToCart).setOnClickListener {
            parentFragmentManager.commit {
                replace(
                    R.id.fragmentContainerFood,
                    CartFragment()
                ).addToBackStack("FoodList")
            }
        }

        return view
    }

    fun showAddedToCartMessage(item: FoodItem){
        Toast.makeText(activity, "${item.name} успешно добавлена", Toast.LENGTH_SHORT).show()
    }
}
