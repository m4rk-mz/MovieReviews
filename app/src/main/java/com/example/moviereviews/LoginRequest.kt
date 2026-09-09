package com.example.moviereviews

// Datos que enviamos a la API para iniciar sesión.
data class LoginRequest(
    val username: String,
    val password: String
)