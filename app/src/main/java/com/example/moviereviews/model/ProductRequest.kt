package com.example.moviereviews.model

// Datos que se envían al registrar un producto.
data class ProductRequest(
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String
)

// Fake Store API devuelve el identificador asignado.
data class ProductCreateResponse(
    val id: Int
)
