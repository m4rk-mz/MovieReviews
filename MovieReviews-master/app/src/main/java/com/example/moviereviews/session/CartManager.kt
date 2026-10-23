package com.example.moviereviews.session

import com.example.moviereviews.model.CartItem
import com.example.moviereviews.model.Product

object CartManager {
    private val cartItems = mutableListOf<CartItem>()

    fun addToCart(product: Product, quantity: Int) {
        val existingItem = cartItems.find { it.product.id == product.id }
        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            cartItems.add(CartItem(product = product, quantity = quantity))
        }
    }

    // Actualiza la cantidad de un producto o lo elimina si llega a 0 (Escenario 1 y 2)
    fun updateQuantity(productId: Int, newQuantity: Int) {
        val item = cartItems.find { it.product.id == productId }
        if (item != null) {
            if (newQuantity <= 0) {
                removeItem(productId)
            } else {
                item.quantity = newQuantity
            }
        }
    }

    // Remueve un artículo del carrito (Escenario 2)
    fun removeItem(productId: Int) {
        cartItems.removeAll { it.product.id == productId }
    }

    // Calcula el total acumulado de la compra (Regla de negocio)
    fun getTotal(): Double {
        return cartItems.sumOf { it.product.price * it.quantity }
    }

    fun getCartItems(): List<CartItem> {
        return cartItems
    }

    // Indica si el carrito no tiene productos (Escenario 3)
    fun isEmpty(): Boolean {
        return cartItems.isEmpty()
    }

    fun clearCart() {
        cartItems.clear()
    }
}