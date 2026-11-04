package com.example.moviereviews.model

// Datos que entrega GET /users desde Supabase.
data class User(
    val id: Int = 0,
    val usuario: String? = null,
    val username: String? = null,
    val email: String? = null
)
