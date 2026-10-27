package com.example.moviereviews.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviereviews.R
import com.example.moviereviews.model.CartItem
import java.util.Locale

class CartAdapter(
    cartItems: List<CartItem>,
    private val onIncreaseClick: (CartItem) -> Unit,
    private val onDecreaseClick: (CartItem) -> Unit,
    private val onRemoveClick: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var cartItems: List<CartItem> = cartItems.toList()

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cartItemImage: ImageView = view.findViewById(R.id.cartItemImage)
        val cartItemTitle: TextView = view.findViewById(R.id.cartItemTitle)
        val cartItemPrice: TextView = view.findViewById(R.id.cartItemPrice)
        val cartItemSubtotal: TextView = view.findViewById(R.id.cartItemSubtotal)
        val cartItemQuantity: TextView = view.findViewById(R.id.cartItemQuantity)
        val btnIncrease: Button = view.findViewById(R.id.btnIncrease)
        val btnDecrease: Button = view.findViewById(R.id.btnDecrease)
        val btnRemoveItem: Button = view.findViewById(R.id.btnRemoveItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        val product = item.product
        val subtotal = product.price * item.quantity

        holder.cartItemTitle.text = product.title
        holder.cartItemPrice.text =
            String.format(Locale.getDefault(), "Precio unitario: $%.2f", product.price)
        holder.cartItemSubtotal.text =
            String.format(Locale.getDefault(), "Subtotal: $%.2f", subtotal)
        holder.cartItemQuantity.text = item.quantity.toString()

        Glide.with(holder.itemView.context)
            .load(product.image)
            .into(holder.cartItemImage)

        holder.btnIncrease.setOnClickListener {
            if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
                onIncreaseClick(item)
            }
        }
        holder.btnDecrease.setOnClickListener {
            if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
                onDecreaseClick(item)
            }
        }
        holder.btnRemoveItem.setOnClickListener {
            if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
                onRemoveClick(item)
            }
        }
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateData(newItems: List<CartItem>) {
        cartItems = newItems.toList()
        notifyDataSetChanged()
    }
}
