package com.example.moviereviews.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereviews.R
import com.example.moviereviews.model.User

class UserAdapter(
    private val users: List<User>
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.userId)
        val username: TextView = view.findViewById(R.id.userUsername)
        val email: TextView = view.findViewById(R.id.userEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.id.text = "ID: ${user.id}"
        holder.username.text = "Usuario: ${user.username ?: user.usuario ?: "No disponible"}"
        holder.email.text = "Correo: ${user.email?.takeIf { it.isNotBlank() } ?: "No registrado"}"
    }

    override fun getItemCount(): Int = users.size
}
