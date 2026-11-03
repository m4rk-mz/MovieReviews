package com.example.moviereviews.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereviews.R
import com.example.moviereviews.adapter.CartHistoryAdapter
import com.example.moviereviews.api.RetrofitClient
import com.example.moviereviews.model.CartHistory
import com.example.moviereviews.model.Product
import com.example.moviereviews.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GlobalCartsActivity : AppCompatActivity() {

    private lateinit var cartsRecyclerView: RecyclerView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var retryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_global_carts)

        val session = SessionManager(this)
        val role = session.getRole()
        if (session.getToken().isNullOrBlank() || role !in listOf("Administrador", "Auditor")) {
            Toast.makeText(this, "No tienes permiso para consultar carritos", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cartsRecyclerView = findViewById(R.id.globalCartsRecyclerView)
        loadingProgress = findViewById(R.id.globalCartsLoadingProgress)
        errorText = findViewById(R.id.globalCartsErrorText)
        retryButton = findViewById(R.id.globalCartsRetryButton)

        findViewById<Button>(R.id.globalCartsBackButton).setOnClickListener { finish() }
        retryButton.setOnClickListener { loadCarts() }
        cartsRecyclerView.layoutManager = LinearLayoutManager(this)

        loadCarts()
    }

    private fun loadCarts() {
        loadingProgress.visibility = View.VISIBLE
        errorText.visibility = View.GONE
        retryButton.visibility = View.GONE
        cartsRecyclerView.visibility = View.GONE

        RetrofitClient.instance.getAllCarts().enqueue(object : Callback<List<CartHistory>> {
            override fun onResponse(
                call: Call<List<CartHistory>>,
                response: Response<List<CartHistory>>
            ) {
                val carts = response.body()
                if (!response.isSuccessful || carts == null) {
                    loadingProgress.visibility = View.GONE
                    showError("No se pudo cargar el histórico de carritos.")
                    return
                }

                // Se cruzan los productId con GET /products. Si falla el catálogo,
                // el adaptador conserva los identificadores recibidos en /carts.
                RetrofitClient.instance.getProducts().enqueue(object : Callback<List<Product>> {
                    override fun onResponse(
                        call: Call<List<Product>>,
                        productsResponse: Response<List<Product>>
                    ) {
                        val names = if (productsResponse.isSuccessful) {
                            productsResponse.body().orEmpty().associate { it.id to it.title }
                        } else {
                            emptyMap()
                        }
                        showCarts(carts, names)
                    }

                    override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                        showCarts(carts, emptyMap())
                    }
                })
            }

            override fun onFailure(call: Call<List<CartHistory>>, t: Throwable) {
                loadingProgress.visibility = View.GONE
                showError("No se pudo conectar con el servidor.")
            }
        })
    }

    private fun showCarts(carts: List<CartHistory>, productNames: Map<Int, String>) {
        loadingProgress.visibility = View.GONE
        cartsRecyclerView.adapter = CartHistoryAdapter(carts, productNames)
        cartsRecyclerView.visibility = View.VISIBLE
        if (carts.isEmpty()) {
            errorText.text = "No hay carritos registrados."
            errorText.visibility = View.VISIBLE
        }
    }

    private fun showError(message: String) {
        errorText.text = message
        errorText.visibility = View.VISIBLE
        retryButton.visibility = View.VISIBLE
    }
}
