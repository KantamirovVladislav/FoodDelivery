package com.example.fooddelivery

object CartManager {
    private val cartItems = mutableMapOf<FoodItem, Int>()

    fun addItem(item: FoodItem) {
        cartItems[item] = (cartItems[item] ?: 0) + 1
    }

    fun removeOne(item: FoodItem) {
        cartItems[item]?.let { count ->
            if (count > 1) {
                cartItems[item] = count - 1
            } else {
                cartItems.remove(item)
            }
        }
    }

    fun removeItem(item: FoodItem) {
        cartItems.remove(item)
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun getCartItems(): Map<FoodItem, Int> {
        return cartItems
    }

    fun getTotalPrice(): Double {
        return cartItems.entries.sumOf { (item, quantity) ->
            item.price * quantity
        }
    }

    fun getTotalQuantity(): Int {
        return cartItems.entries.sumOf { (item, quantity) ->
            quantity
        }
    }
}
