package com.example.moviereviews

import android.content.Context

// Guarda los datos de la sesión en el dispositivo.
class SessionManager(context: Context) {

    private val preferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Guarda token, ID y rol.
    fun saveSession(token: String, userId: Int, role: String) {

        preferences.edit()
            .putString("token", token)
            .putInt("userId", userId)
            .putString("role", role)
            .apply()
    }

    // Obtiene el token guardado.
    fun getToken(): String? {
        return preferences.getString("token", null)
    }

    // Obtiene el rol guardado.
    fun getRole(): String? {
        return preferences.getString("role", null)
    }

    // Obtiene el ID guardado.
    fun getUserId(): Int {
        return preferences.getInt("userId", -1)
    }

    // Borra completamente la sesión.
    fun clearSession() {
        preferences.edit().clear().apply()
    }
}