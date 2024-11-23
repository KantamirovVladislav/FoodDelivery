package com.example.fooddelivery

data class FoodItem(
    var id: String,
    var name: String,
    var price: Double,
    var imageLink: String
) {
    constructor() : this(
        "",
        "",
        0.0,
        ""
    )
}
