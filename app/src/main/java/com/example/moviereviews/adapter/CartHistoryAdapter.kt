package com.example.moviereviews.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereviews.R
import com.example.moviereviews.model.CartHistory

class CartHistoryAdapter(
    private val carts: List<CartHistory>,
    private val productNames: Map<Int, String>
) : RecyclerView.Adapter<CartHistoryAdapter.CartHistoryViewHolder>() {

    private val expandedCarts = mutableSetOf<Int>()

    class CartHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cartId: TextView = view.findViewById(R.id.historyCartId)
        val cartDate: TextView = view.findViewById(R.id.historyCartDate)
        val userId: TextView = view.findViewById(R.id.historyCartUser)
        val expandButton: Button = view.findViewById(R.id.historyExpandButton)
        val productsDetail: TextView = view.findViewById(R.id.historyProductsDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_cart, parent, false)
        return CartHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartHistoryViewHolder, position: Int) {
        val cart = carts[position]
        val expanded = expandedCarts.contains(cart.id)

        holder.cartId.text = "Carrito #${cart.id}"
        holder.cartDate.text = "Fecha: ${cart.date ?: "No disponible"}"
        holder.userId.text = "Usuario propietario: #${cart.userId}"
        holder.expandButton.text = if (expanded) "OCULTAR ARTÍCULOS" else "VER ARTÍCULOS"
        holder.productsDetail.visibility = if (expanded) View.VISIBLE else View.GONE

        if (expanded) {
            holder.productsDetail.text = if (cart.products.isEmpty()) {
                "Este carrito no contiene artículos."
            } else {
                cart.products.joinToString("\n") { item ->
                    val name = productNames[item.productId]
                        ?: "Producto #${item.productId}"
                    "• $name — Cantidad: ${item.quantity}"
                }
            }
        }

        holder.expandButton.setOnClickListener {
            if (expandedCarts.contains(cart.id)) {
                expandedCarts.remove(cart.id)
            } else {
                expandedCarts.add(cart.id)
            }
            notifyItemChanged(holder.bindingAdapterPosition)
        }
    }

    override fun getItemCount(): Int = carts.size
}
