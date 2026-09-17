package com.example.moviereviews.api

import com.example.moviereviews.model.LoginRequest
import com.example.moviereviews.model.LoginResponse
import com.example.moviereviews.model.Product
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Aquí definimos las peticiones a Fake Store API
interface ApiService {

    // Iniciar sesión
    @POST("auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    // Obtener catálogo de productos
    @GET("products")
    fun getProducts(): Call<List<Product>>
}