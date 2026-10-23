package com.example.moviereviews.model

import com.google.gson.annotations.SerializedName

// Datos que enviamos a la API para iniciar sesión.
data class LoginRequest(
    @SerializedName("usuario")
    val username: String,
    val password: String
)