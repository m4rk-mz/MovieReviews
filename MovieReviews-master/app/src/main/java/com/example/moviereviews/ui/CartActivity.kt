package com.example.moviereviews.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereviews.R
import com.example.moviereviews.adapter.CartAdapter
import com.example.moviereviews.api.ApiService
import com.example.moviereviews.api.RetrofitClient
import com.example.moviereviews.model.AddToCartRequest
import com.example.moviereviews.model.AddToCartResponse
import com.example.moviereviews.model.CartItem
import com.example.moviereviews.model.CartProductRequest
import com.example.moviereviews.session.CartManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var emptyCartContainer: LinearLayout
    private lateinit var cartSummaryContainer: LinearLayout
    private lateinit var cartTotalText: TextView
    private lateinit var checkoutButton: Button
    private lateinit var cartBackButton: Button

    private lateinit var cartAdapter: CartAdapter
    private lateinit var api: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        emptyCartContainer = findViewById(R.id.emptyCartContainer)
        cartSummaryContainer = findViewById(R.id.cartSummaryContainer)
        cartTotalText = findViewById(R.id.cartTotalText)
        checkoutButton = findViewById(R.id.checkoutButton)
        cartBackButton = findViewById(R.id.cartBackButton)

        api = RetrofitClient.instance

        cartBackButton.setOnClickListener { finish() }

        setupRecyclerView()
        updateCartState()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            cartItems = CartManager.getCartItems(),
            onIncreaseClick = { item -> changeQuantity(item, item.quantity + 1) },
            onDecreaseClick = { item -> changeQuantity(item, item.quantity - 1) },
            onRemoveClick = { item -> removeItem(item) }
        )

        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = cartAdapter
    }

    // Actualizar o modificar cantidad (Escenario 1)
    private fun changeQuantity(item: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItem(item)
            return
        }

        CartManager.updateQuantity(item.product.id, newQuantity)
        executePutApi(item.product.id, newQuantity)
        updateCartState()
    }

    // Remoción de producto (Escenario 2)
    private fun removeItem(item: CartItem) {
        CartManager.removeItem(item.product.id)
        executeDeleteApi(item.product.id)
        updateCartState()
    }

    // Recálculo del total y control de interfaz vacía (Escenarios 1 y 3)
    private fun updateCartState() {
        val items = CartManager.getCartItems()
        cartAdapter.updateData(items)

        if (CartManager.isEmpty()) {
            // Escenario 3: Carrito vacío
            emptyCartContainer.visibility = View.VISIBLE
            cartRecyclerView.visibility = View.GONE
            cartSummaryContainer.visibility = View.GONE
            checkoutButton.isEnabled = false
        } else {
            emptyCartContainer.visibility = View.GONE
            cartRecyclerView.visibility = View.VISIBLE
            cartSummaryContainer.visibility = View.VISIBLE
            checkoutButton.isEnabled = true

            // Formatear precio redondeado a 2 decimales
            val total = CartManager.getTotal()
            cartTotalText.text = "$" + String.format("%.2f", total)
        }
    }

    // Consumo del endpoint PUT /carts/{id} (Escenario 1)
    private fun executePutApi(productId: Int, quantity: Int) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val request = AddToCartRequest(
            userId = 1,
            date = currentDate,
            products = listOf(CartProductRequest(productId = productId, quantity = quantity))
        )

        api.updateCart(productId, request).enqueue(object : Callback<AddToCartResponse> {
            override fun onResponse(call: Call<AddToCartResponse>, response: Response<AddToCartResponse>) {}
            override fun onFailure(call: Call<AddToCartResponse>, t: Throwable) {}
        })
    }

    // Consumo del endpoint DELETE /carts/{id} (Escenario 2)
    private fun executeDeleteApi(productId: Int) {
        api.deleteCart(productId).enqueue(object : Callback<AddToCartResponse> {
            override fun onResponse(call: Call<AddToCartResponse>, response: Response<AddToCartResponse>) {}
            override fun onFailure(call: Call<AddToCartResponse>, t: Throwable) {}
        })
    }
}