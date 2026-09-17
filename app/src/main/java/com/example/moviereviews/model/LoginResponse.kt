package com.example.moviereviews.model

// Respuesta que recibimos de la API cuando el login es correcto.
data class LoginResponse(
    val token: String
)