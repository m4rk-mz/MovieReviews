package com.example.moviereviews.api

import com.example.moviereviews.model.AddToCartRequest
import com.example.moviereviews.model.AddToCartResponse
import com.example.moviereviews.model.LoginRequest
import com.example.moviereviews.model.LoginResponse
import com.example.moviereviews.model.Product
import com.example.moviereviews.model.User
import com.example.moviereviews.model.CartHistory
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Header

interface ApiService {

    // Autenticación
    @POST("auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    // Carrito (US09): se registra el carrito completo al finalizar la compra.
    @POST("carts")
    fun addToCart(
        @Header("Authorization") authorization: String,
        @Body request: AddToCartRequest
    ): Call<AddToCartResponse>

    // US11: listar usuarios registrados (requiere token de Administrador o Auditor).
    @GET("admin/users")
    fun getUsers(
        @Header("Authorization") authorization: String
    ): Call<List<User>>

    // US12: consultar el histórico global de carritos (solo lectura).
    @GET("admin/carts")
    fun getAllCarts(
        @Header("Authorization") authorization: String
    ): Call<List<CartHistory>>

    // Catálogo
    @GET("products")
    fun getProducts(): Call<List<Product>>

    @GET("products/categories")
    fun getCategories(): Call<List<String>>

    @GET("products/category/{category}")
    fun getProductsByCategory(
        @Path("category") category: String
    ): Call<List<Product>>

    @GET("products/{id}")
    fun getProduct(
        @Path("id") id: Int
    ): Call<Product>

    @PUT("products/{id}")
    fun updateProduct(
        @Path("id") id: Int,
        @Body product: Product
    ): Call<Product>

    @DELETE("products/{id}")
    fun deleteProduct(
        @Path("id") id: Int
    ): Call<Product>
}
