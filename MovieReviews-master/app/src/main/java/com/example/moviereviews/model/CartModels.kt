package com.example.moviereviews.model

// Modelo individual para la petición a la Fake Store API
data class CartProductRequest(
    val productId: Int,
    val quantity: Int
)

// Modelo para la solicitud POST /carts
data class AddToCartRequest(
    val userId: Int,
    val date: String,
    val products: List<CartProductRequest>
)

// Modelo para la respuesta de la Fake Store API
data class AddToCartResponse(
    val id: Int,
    val userId: Int,
    val date: String,
    val products: List<CartProductRequest>
)

// Modelo de datos para manejar el estado local del carrito en el dispositivo
data class CartItem(
    val product: Product,
    var quantity: Int
)