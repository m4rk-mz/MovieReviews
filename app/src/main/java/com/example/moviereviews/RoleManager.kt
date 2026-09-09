package com.example.moviereviews

// Se encarga de asignar el rol según el ID del usuario.
object RoleManager {

    fun getRole(userId: Int): String {

        return when (userId) {
            1, 2 -> "Administrador"
            3 -> "Auditor"
            else -> "Cliente"
        }
    }
}