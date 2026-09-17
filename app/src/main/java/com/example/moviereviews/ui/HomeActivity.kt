package com.example.moviereviews.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereviews.R
import com.example.moviereviews.adapter.ProductAdapter
import com.example.moviereviews.api.ApiService
import com.example.moviereviews.model.Product
import com.example.moviereviews.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var catalogErrorText: TextView
    private lateinit var retryButton: Button
    private lateinit var apiStatusText: TextView

    private lateinit var api: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        // Elementos de la cuenta
        val welcomeText =
            findViewById<TextView>(R.id.welcomeText)

        val usernameText =
            findViewById<TextView>(R.id.usernameText)

        val passwordText =
            findViewById<TextView>(R.id.passwordText)

        val roleText =
            findViewById<TextView>(R.id.roleText)

        val apiCard =
            findViewById<LinearLayout>(R.id.apiCard)

        val accountCard =
            findViewById<LinearLayout>(R.id.accountCard)

        val logoutButton =
            findViewById<Button>(R.id.logoutButton)

        // Elementos del catálogo
        productsRecyclerView =
            findViewById(R.id.productsRecyclerView)

        loadingProgress =
            findViewById(R.id.loadingProgress)

        catalogErrorText =
            findViewById(R.id.catalogErrorText)

        retryButton =
            findViewById(R.id.retryButton)

        apiStatusText =
            findViewById(R.id.apiStatusText)

        // -------------------------
        // SESIÓN
        // -------------------------

        val sessionManager =
            SessionManager(this)

        val role =
            sessionManager.getRole()

        val username =
            intent.getStringExtra("username")
                ?: "Usuario"

        val password =
            intent.getStringExtra("password")
                ?: ""

        welcomeText.text =
            "Bienvenido, $username"

        usernameText.text =
            username

        roleText.text =
            role ?: "Administrador"

        if (password.isNotEmpty()) {

            passwordText.text =
                "•".repeat(password.length)

        } else {

            passwordText.text =
                "••••••••"
        }

        // -------------------------
        // RECYCLERVIEW
        // -------------------------

        productsRecyclerView.layoutManager =
            LinearLayoutManager(this)

        // -------------------------
        // RETROFIT
        // -------------------------

        val retrofit =
            Retrofit.Builder()
                .baseUrl("https://fakestoreapi.com/")
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()

        api =
            retrofit.create(ApiService::class.java)

        // Cargar catálogo
        loadProducts()

        // Botón Reintentar
        retryButton.setOnClickListener {

            loadProducts()
        }

        // -------------------------
        // ANIMACIONES
        // -------------------------

        val fadeIn =
            AnimationUtils.loadAnimation(
                this,
                R.anim.fade_in
            )

        val slideUpApi =
            AnimationUtils.loadAnimation(
                this,
                R.anim.slide_up
            )

        val slideUpAccount =
            AnimationUtils.loadAnimation(
                this,
                R.anim.slide_up
            )

        val fadeButton =
            AnimationUtils.loadAnimation(
                this,
                R.anim.fade_in
            )

        slideUpApi.startOffset = 200
        slideUpAccount.startOffset = 450
        fadeButton.startOffset = 700

        welcomeText.startAnimation(fadeIn)
        apiCard.startAnimation(slideUpApi)
        accountCard.startAnimation(slideUpAccount)
        logoutButton.startAnimation(fadeButton)

        // -------------------------
        // CERRAR SESIÓN
        // -------------------------

        logoutButton.setOnClickListener {

            sessionManager.clearSession()

            val intent =
                Intent(
                    this,
                    MainActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            overridePendingTransition(
                R.anim.fade_in,
                R.anim.fade_out
            )

            finish()
        }
    }

    // -------------------------
    // CARGAR PRODUCTOS
    // -------------------------

    private fun loadProducts() {

        // Mostrar Loading
        loadingProgress.visibility =
            View.VISIBLE

        // Ocultar error
        catalogErrorText.visibility =
            View.GONE

        retryButton.visibility =
            View.GONE

        productsRecyclerView.visibility =
            View.GONE

        apiStatusText.text =
            "●  Conectando..."

        api.getProducts()
            .enqueue(
                object : Callback<List<Product>> {

                    override fun onResponse(
                        call: Call<List<Product>>,
                        response: Response<List<Product>>
                    ) {

                        loadingProgress.visibility =
                            View.GONE

                        if (response.isSuccessful) {

                            val products =
                                response.body()

                            if (products != null) {

                                // Mostrar catálogo
                                productsRecyclerView.adapter =
                                    ProductAdapter(products)

                                productsRecyclerView.visibility =
                                    View.VISIBLE

                                apiStatusText.text =
                                    "●  Conectado"

                            } else {

                                showCatalogError()
                            }

                        } else {

                            showCatalogError()
                        }
                    }

                    override fun onFailure(
                        call: Call<List<Product>>,
                        t: Throwable
                    ) {

                        loadingProgress.visibility =
                            View.GONE

                        showCatalogError()
                    }
                }
            )
    }

    // -------------------------
    // ERROR DEL CATÁLOGO
    // -------------------------

    private fun showCatalogError() {

        apiStatusText.text =
            "●  Sin conexión"

        productsRecyclerView.visibility =
            View.GONE

        catalogErrorText.text =
            "No se pudo cargar el catálogo"

        catalogErrorText.visibility =
            View.VISIBLE

        retryButton.visibility =
            View.VISIBLE
    }
}