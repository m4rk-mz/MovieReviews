package com.example.moviereviews.api

import com.example.moviereviews.model.LoginRequest
import com.example.moviereviews.model.LoginResponse
import com.example.moviereviews.model.Product
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Aquí definimos las peticiones a Fake Store API
interface ApiService {

    // Iniciar sesión
    @POST("auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    // Obtener catálogo completo
    @GET("products")
    fun getProducts(): Call<List<Product>>

    // Obtener categorías disponibles
    @GET("products/categories")
    fun getCategories(): Call<List<String>>

    // Obtener productos de una categoría
    @GET("products/category/{category}")
    fun getProductsByCategory(
        @Path("category") category: String
    ): Call<List<Product>>

    // Obtener el detalle de un producto
    @GET("products/{id}")
    fun getProduct(
        @Path("id") id: Int
    ): Call<Product>

    // Editar un producto
    @PUT("products/{id}")
    fun updateProduct(
        @Path("id") id: Int,
        @Body product: Product
    ): Call<Product>

    // Eliminar un producto
    @DELETE("products/{id}")
    fun deleteProduct(
        @Path("id") id: Int
    ): Call<Product>
}