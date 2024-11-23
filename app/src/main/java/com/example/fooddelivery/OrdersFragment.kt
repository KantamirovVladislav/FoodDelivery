package com.example.fooddelivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth

class OrdersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrdersAdapter
    private val db = Firebase.firestore
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_orders, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewOrders)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        if (currentUser != null) {
            val email = currentUser.email ?: ""
            if (email.isNotBlank()) {
                loadOrders(email)
            }
        }

        return view
    }

    private fun loadOrders(email: String) {
        db.collection("Orders")
            .whereEqualTo("emailUser", email)
            .get()
            .addOnSuccessListener { documents ->
                val orders = mutableListOf<OrderInfo>()
                for (document in documents) {
                    val order = document.toObject(OrderInfo::class.java)
                    orders.add(order)
                }
                adapter = OrdersAdapter(orders)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                println("Error loading orders: $e")
            }
    }
}
