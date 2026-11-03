package com.example.moviereviews.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereviews.R
import com.example.moviereviews.adapter.ProductAdapter
import com.example.moviereviews.api.ApiService
import com.example.moviereviews.api.RetrofitClient
import com.example.moviereviews.model.Product
import com.example.moviereviews.session.CartManager
import com.example.moviereviews.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var catalogErrorText: TextView
    private lateinit var retryButton: Button
    private lateinit var apiStatusText: TextView
    private lateinit var categorySpinner: Spinner

    private lateinit var api: ApiService
    private var selectedCategory: String? = null
    private var hasLoadedCatalog = false

    override fun onResume() {
        super.onResume()
        // Al volver del detalle, vuelve a consultar el servidor para reflejar cambios.
        if (hasLoadedCatalog) {
            loadProducts(selectedCategory)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        // Elementos de la cuenta
        val welcomeText = findViewById<TextView>(R.id.welcomeText)
        val usernameText = findViewById<TextView>(R.id.usernameText)
        val passwordText = findViewById<TextView>(R.id.passwordText)
        val roleText = findViewById<TextView>(R.id.roleText)
        val apiCard = findViewById<LinearLayout>(R.id.apiCard)
        val accountCard = findViewById<LinearLayout>(R.id.accountCard)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val cartButton = findViewById<Button>(R.id.cartButton)
        val adminAuditMenu = findViewById<LinearLayout>(R.id.adminAuditMenu)
        val usersButton = findViewById<Button>(R.id.usersButton)
        val globalCartsButton = findViewById<Button>(R.id.globalCartsButton)

        // Elementos del catálogo
        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        loadingProgress = findViewById(R.id.loadingProgress)
        catalogErrorText = findViewById(R.id.catalogErrorText)
        retryButton = findViewById(R.id.retryButton)
        apiStatusText = findViewById(R.id.apiStatusText)
        categorySpinner = findViewById(R.id.categorySpinner)

        // Sesión y control de roles
        val sessionManager = SessionManager(this)
        val role = sessionManager.getRole()
        val username = intent.getStringExtra("username") ?: "Usuario"
        val password = intent.getStringExtra("password") ?: ""

        welcomeText.text = "Bienvenido, $username"
        usernameText.text = username
        roleText.text = role ?: "Sin rol"

        passwordText.text = if (password.isNotEmpty()) {
            "•".repeat(password.length)
        } else {
            "••••••••"
        }

        // US11 y US12: las opciones solo se muestran a Administrador y Auditor.
        val canAudit = role == "Administrador" || role == "Auditor"
        adminAuditMenu.visibility = if (canAudit) View.VISIBLE else View.GONE

        if (canAudit) {
            usersButton.setOnClickListener {
                startActivity(Intent(this, UsersActivity::class.java))
            }
            globalCartsButton.setOnClickListener {
                startActivity(Intent(this, GlobalCartsActivity::class.java))
            }
        }

        // El Auditor no administra ni consulta el carrito personal.
        if (role == "Auditor") {
            cartButton.visibility = View.GONE
        } else {
            cartButton.visibility = View.VISIBLE
            cartButton.setOnClickListener {
                startActivity(Intent(this, CartActivity::class.java))
            }
        }

        productsRecyclerView.layoutManager = LinearLayoutManager(this)
        api = RetrofitClient.instance
        loadCategories()

        retryButton.setOnClickListener {
            loadProducts(selectedCategory)
        }

        // Animaciones
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUpApi = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val slideUpAccount = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val fadeButton = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        slideUpApi.startOffset = 200
        slideUpAccount.startOffset = 450
        fadeButton.startOffset = 700

        welcomeText.startAnimation(fadeIn)
        apiCard.startAnimation(slideUpApi)
        accountCard.startAnimation(slideUpAccount)
        logoutButton.startAnimation(fadeButton)

        // Cerrar sesión
        logoutButton.setOnClickListener {
            sessionManager.clearSession()
            CartManager.clearCart()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }
    }

    private fun loadCategories() {
        showLoading()

        api.getCategories().enqueue(object : Callback<List<String>> {
            override fun onResponse(
                call: Call<List<String>>,
                response: Response<List<String>>
            ) {
                val categories = response.body()
                if (response.isSuccessful && categories != null) {
                    setupCategorySpinner(categories)
                } else {
                    loadProducts()
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                loadProducts()
            }
        })
    }

    private fun setupCategorySpinner(categories: List<String>) {
        val options = listOf("Ver todos") + categories
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        categorySpinner.adapter = adapter
        categorySpinner.visibility = View.VISIBLE
        categorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCategory = if (position == 0) null else options[position]
                    loadProducts(selectedCategory)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
    }

    private fun loadProducts(category: String? = null) {
        showLoading()
        productsRecyclerView.adapter = null

        val request: Call<List<Product>> =
            if (category == null) api.getProducts() else api.getProductsByCategory(category)

        request.enqueue(object : Callback<List<Product>> {
            override fun onResponse(
                call: Call<List<Product>>,
                response: Response<List<Product>>
            ) {
                loadingProgress.visibility = View.GONE
                val products = response.body()

                if (response.isSuccessful && products != null) {
                    hasLoadedCatalog = true
                    productsRecyclerView.adapter = ProductAdapter(products) { product ->
                        val intent = Intent(this@HomeActivity, ProductDetailActivity::class.java)
                        intent.putExtra("productId", product.id)
                        startActivity(intent)
                    }
                    productsRecyclerView.visibility = View.VISIBLE
                    apiStatusText.text = "●  Conectado"
                } else {
                    showCatalogError()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                loadingProgress.visibility = View.GONE
                showCatalogError()
            }
        })
    }

    private fun showLoading() {
        loadingProgress.visibility = View.VISIBLE
        catalogErrorText.visibility = View.GONE
        retryButton.visibility = View.GONE
        productsRecyclerView.visibility = View.GONE
        apiStatusText.text = "●  Conectando..."
    }

    private fun showCatalogError() {
        apiStatusText.text = "●  Sin conexión"
        productsRecyclerView.visibility = View.GONE
        catalogErrorText.text = "No se pudo cargar el catálogo"
        catalogErrorText.visibility = View.VISIBLE
        retryButton.visibility = View.VISIBLE
    }
}