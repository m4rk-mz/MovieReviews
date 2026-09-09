package com.example.moviereviews

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Base64
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Elementos del Login
        val usernameInput =
            findViewById<EditText>(R.id.usernameInput)

        val passwordInput =
            findViewById<EditText>(R.id.passwordInput)

        val loginButton =
            findViewById<Button>(R.id.loginButton)

        val errorText =
            findViewById<TextView>(R.id.errorText)

        // Animaciones del Login
        val fadeIn =
            AnimationUtils.loadAnimation(
                this,
                R.anim.fade_in
            )

        val slideUp =
            AnimationUtils.loadAnimation(
                this,
                R.anim.slide_up
            )

        usernameInput.startAnimation(slideUp)
        passwordInput.startAnimation(slideUp)
        loginButton.startAnimation(fadeIn)

        // Retrofit
        val retrofit =
            Retrofit.Builder()
                .baseUrl("https://fakestoreapi.com/")
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()

        val api =
            retrofit.create(ApiService::class.java)

        // Botón iniciar sesión
        loginButton.setOnClickListener {

            errorText.text = ""

            val username =
                usernameInput.text
                    .toString()
                    .trim()

            val password =
                passwordInput.text
                    .toString()

            // Campos vacíos
            if (
                username.isEmpty() ||
                password.isEmpty()
            ) {

                errorText.text =
                    "Completa usuario y contraseña"

                return@setOnClickListener
            }

            // Sin Internet
            if (!hasInternetConnection()) {

                errorText.text =
                    "No hay conexión a Internet"

                return@setOnClickListener
            }

            // Datos para Fake Store API
            val loginRequest =
                LoginRequest(
                    username = username,
                    password = password
                )

            // Petición Login
            api.login(loginRequest)
                .enqueue(
                    object :
                        Callback<LoginResponse> {

                        override fun onResponse(
                            call: Call<LoginResponse>,
                            response: Response<LoginResponse>
                        ) {

                            if (
                                response.isSuccessful
                            ) {

                                val token =
                                    response
                                        .body()
                                        ?.token

                                if (token != null) {

                                    val userId =
                                        getUserIdFromToken(
                                            token
                                        )

                                    if (
                                        userId != null
                                    ) {

                                        // Asignar rol
                                        val role =
                                            RoleManager.getRole(
                                                userId
                                            )

                                        // Guardar sesión
                                        val sessionManager =
                                            SessionManager(
                                                this@MainActivity
                                            )

                                        sessionManager
                                            .saveSession(
                                                token = token,
                                                userId = userId,
                                                role = role
                                            )

                                        // Abrir Home
                                        val intent =
                                            Intent(
                                                this@MainActivity,
                                                HomeActivity::class.java
                                            )

                                        // Mandar usuario
                                        intent.putExtra(
                                            "username",
                                            username
                                        )

                                        // Mandar contraseña
                                        intent.putExtra(
                                            "password",
                                            password
                                        )

                                        startActivity(intent)

                                        // Transición entre pantallas
                                        overridePendingTransition(
                                            R.anim.slide_in_right,
                                            R.anim.fade_out
                                        )

                                        finish()

                                    } else {

                                        errorText.text =
                                            "No se pudo identificar al usuario"
                                    }

                                } else {

                                    errorText.text =
                                        "No se recibió el token"
                                }

                            } else {

                                errorText.text =
                                    "Usuario o contraseña inválidos"
                            }
                        }

                        override fun onFailure(
                            call: Call<LoginResponse>,
                            t: Throwable
                        ) {

                            errorText.text =
                                "Error al conectar con el servidor"
                        }
                    }
                )
        }
    }

    // Comprobar Internet
    private fun hasInternetConnection(): Boolean {

        val connectivityManager =
            getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager

        val network =
            connectivityManager.activeNetwork
                ?: return false

        val capabilities =
            connectivityManager
                .getNetworkCapabilities(
                    network
                )
                ?: return false

        return capabilities
            .hasCapability(
                NetworkCapabilities
                    .NET_CAPABILITY_INTERNET
            )
    }

    // Obtener ID desde el token
    private fun getUserIdFromToken(
        token: String
    ): Int? {

        return try {

            val parts =
                token.split(".")

            if (parts.size < 2) {
                return null
            }

            val payload =
                String(
                    Base64.decode(
                        parts[1],
                        Base64.URL_SAFE or
                                Base64.NO_WRAP or
                                Base64.NO_PADDING
                    )
                )

            val json =
                JSONObject(payload)

            json.getInt("sub")

        } catch (e: Exception) {

            null
        }
    }
}