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
        val name: TextView = view.findViewById(R.id.userFullName)
        val email: TextView = view.findViewById(R.id.userEmail)
        val phone: TextView = view.findViewById(R.id.userPhone)
        val username: TextView = view.findViewById(R.id.userUsername)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        val fullName = listOfNotNull(
            user.name?.firstname?.takeIf { it.isNotBlank() },
            user.name?.lastname?.takeIf { it.isNotBlank() }
        ).joinToString(" ").ifBlank { "Nombre no disponible" }

        holder.name.text = fullName
        holder.email.text = "Correo: ${user.email ?: "No disponible"}"
        holder.phone.text = "Teléfono: ${user.phone ?: "No disponible"}"
        holder.username.text = "Usuario: ${user.username ?: "No disponible"}"
    }

    override fun getItemCount(): Int = users.size
}
