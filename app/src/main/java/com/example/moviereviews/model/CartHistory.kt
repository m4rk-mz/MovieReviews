package com.example.moviereviews.model

// Datos que entrega GET /carts desde Supabase.
data class CartHistory(
    val id: Int = 0,
    val userId: Int = 0,
    val date: String? = null,
    val products: List<CartHistoryProduct> = emptyList()
)

data class CartHistoryProduct(
    val productId: Int = 0,
    val quantity: Int = 0
)
