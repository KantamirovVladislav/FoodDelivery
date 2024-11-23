package com.example.fooddelivery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class CartFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CartItemAdapter
    lateinit var createOrder: Button
    lateinit var outputTotalQuantity: TextView
    lateinit var outputTotalPrice: TextView
    lateinit var inputAddress: EditText
    private var selectedTime: Calendar? = null
    private var addressUser: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val email = currentUser.email?.lowercase()

            val db = FirebaseFirestore.getInstance()
            db.collection("Users")
                .whereEqualTo("email", email?.lowercase())
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents.first()
                        val userEmail = document.getString("email")
                        addressUser = document.getString("address").toString()

                        inputAddress.setText(addressUser)
                    } else {
                        println("No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error getting document: $exception")
                }
        } else {
            println("No user is currently signed in.")
        }

        if (addressUser.isNotBlank()) {
            inputAddress.setText(addressUser)
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        createOrder = view.findViewById(R.id.createOrder)
        outputTotalQuantity = view.findViewById(R.id.textViewForCounterPosition)
        outputTotalPrice = view.findViewById(R.id.textViewForTotalPrice)
        inputAddress = view.findViewById(R.id.editTextForAddress)

        view.findViewById<Button>(R.id.checkOrder).setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainerFood, OrdersFragment()).addToBackStack(null)
            }
        }
        updateCart()

//        view.findViewById<Button>(R.id.buttonClearCart).setOnClickListener {
//            CartManager.clearCart()
//            updateCart()
//        }

        createOrder.setOnClickListener {
            showTimePicker()
        }


        return view
    }

    private fun updateCart() {
        val cartItems = CartManager.getCartItems()
        adapter = CartItemAdapter(
            cartItems,
            onIncrease = { item ->
                CartManager.addItem(item)
                updateCart()
            },
            onDecrease = { item ->
                CartManager.removeOne(item)
                updateCart()
            },
            onRemove = { item ->
                CartManager.removeItem(item)
                updateCart()
            }
        )
        recyclerView.adapter = adapter
        outputTotalQuantity.text = "В корзине: " + CartManager.getTotalQuantity().toString()
        outputTotalPrice.text = "К оплате " + CartManager.getTotalPrice().toString()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePicker = MaterialTimePicker.Builder()
            .setHour(currentHour)
            .setMinute(currentMinute)
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val selectedHour = timePicker.hour
            val selectedMinute = timePicker.minute
            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour)
            selectedTime.set(Calendar.MINUTE, selectedMinute)

            val currentTime = Calendar.getInstance()


            if (selectedTime.before(currentTime)) {
                Toast.makeText(requireContext(), "Выбранное время не может быть в прошлом", Toast.LENGTH_SHORT).show()
                return@addOnPositiveButtonClickListener
            }

            this.selectedTime = selectedTime

            createOrder(selectedTime)
        }

        timePicker.show(parentFragmentManager, timePicker.toString())
    }
    fun createOrder(selectedTime: Calendar?) {
        val db = Firebase.firestore
        val auth = Firebase.auth
        val cartItems = CartManager.getCartItems()
        val deliveryTime = selectedTime?.time ?: java.util.Date()

        val newAddress = inputAddress.text.toString()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email ?: ""

            if (email.isBlank()) {
                println("User email is null or blank.")
                return
            }

            val userRef = db.collection("Users")
                .whereEqualTo("email", email.lowercase())

            userRef.get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents.first()
                    if (newAddress.isNotBlank() && newAddress != addressUser) {

                        document.reference.update("address", newAddress)
                            .addOnSuccessListener {
                                println("Address updated successfully.")
                                addressUser = newAddress
                                inputAddress.setText(newAddress)
                            }
                            .addOnFailureListener { e ->
                                println("Error updating address: $e")
                            }
                    }
                } else {
                    println("User with email $email not found.")
                }
            }
                .addOnFailureListener { e ->
                    println("Error checking user document: $e")
                }
        }

        if (cartItems.isNotEmpty() && newAddress.isNotBlank()) {
            db.collection("Orders").add(
                OrderInfo(
                    emailUser = auth.currentUser?.email,
                    products = CartManager.getCartItems().map { (foodItem, quantity) ->
                        foodItem.id to quantity
                    }.toMap(),
                    deliveryDateTime = Timestamp(deliveryTime),
                    totalPrice = CartManager.getTotalPrice(),
                    delivered = false,
                    createdOrderAt = Timestamp.now()
                )
            )
            Toast.makeText(activity, "Заказ успешно офрмлен", Toast.LENGTH_SHORT).show()
        }
    }


}
