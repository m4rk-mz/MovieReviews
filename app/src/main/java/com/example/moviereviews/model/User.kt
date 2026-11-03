package com.example.moviereviews.model

// Modelos para mapear la respuesta anidada de GET /users.
data class User(
    val id: Int = 0,
    val email: String? = null,
    val username: String? = null,
    val name: UserName? = null,
    val phone: String? = null,
    val address: UserAddress? = null
)

data class UserName(
    val firstname: String? = null,
    val lastname: String? = null
)

data class UserAddress(
    val city: String? = null,
    val street: String? = null,
    val number: Int? = null,
    val zipcode: String? = null,
    val geolocation: UserGeolocation? = null
)

data class UserGeolocation(
    val lat: String? = null,
    val long: String? = null
)
