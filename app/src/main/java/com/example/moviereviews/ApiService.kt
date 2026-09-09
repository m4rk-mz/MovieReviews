package com.example.moviereviews

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// Aquí definimos las peticiones que haremos a Fake Store API
interface ApiService {

    @POST("auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>
}