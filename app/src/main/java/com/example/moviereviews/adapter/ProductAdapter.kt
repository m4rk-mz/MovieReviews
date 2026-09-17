package com.example.moviereviews.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviereviews.R
import com.example.moviereviews.model.Product

class ProductAdapter(
    private val products: List<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val image: ImageView =
            view.findViewById(R.id.productImage)

        val title: TextView =
            view.findViewById(R.id.productTitle)

        val price: TextView =
            view.findViewById(R.id.productPrice)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_product,
                parent,
                false
            )

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {

        val product = products[position]

        holder.title.text = product.title

        holder.price.text =
            "$${String.format("%.2f", product.price)}"

        Glide.with(holder.itemView.context)
            .load(product.image)
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return products.size
    }
}