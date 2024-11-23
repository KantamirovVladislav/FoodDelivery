package com.example.fooddelivery

import com.google.firebase.Timestamp

data class OrderInfo(
    val emailUser: String?,
    val products: Map<String, Int>,
    val deliveryDateTime: Timestamp,
    val totalPrice: Double,
    val delivered: Boolean,
    val createdOrderAt: Timestamp
){
    constructor():this("", emptyMap(),Timestamp.now(),0.0,false,Timestamp.now())
}
