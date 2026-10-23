package com.example.moviereviews.api

import com.example.moviereviews.model.AddToCartRequest
import com.example.moviereviews.model.AddToCartResponse
import com.example.moviereviews.model.LoginRequest
import com.example.moviereviews.model.LoginResponse
import com.example.moviereviews.model.Product
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // Autenticación
    @POST("auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    // Carrito (US09 y US10)
    @POST("carts")
    fun addToCart(
        @Body request: AddToCartRequest
    ): Call<AddToCartResponse>

    @PUT("carts/{id}")
    fun updateCart(
        @Path("id") id: Int,
        @Body request: AddToCartRequest
    ): Call<AddToCartResponse>

    @DELETE("carts/{id}")
    fun deleteCart(
        @Path("id") id: Int
    ): Call<AddToCartResponse>

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
    ): Call<ResponseBody>

    @DELETE("products/{id}")
    fun deleteProduct(
        @Path("id") id: Int
    ): Call<ResponseBody>
}