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
import com.example.moviereviews.adapter.UserAdapter
import com.example.moviereviews.api.RetrofitClient
import com.example.moviereviews.model.User
import com.example.moviereviews.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsersActivity : AppCompatActivity() {

    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var retryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_users)

        val session = SessionManager(this)
        val role = session.getRole()
        if (session.getToken().isNullOrBlank() || role != "Administrador" && role != "Auditor") {
            Toast.makeText(this, "No tienes permiso para consultar usuarios", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        usersRecyclerView = findViewById(R.id.usersRecyclerView)
        loadingProgress = findViewById(R.id.usersLoadingProgress)
        errorText = findViewById(R.id.usersErrorText)
        retryButton = findViewById(R.id.usersRetryButton)

        findViewById<Button>(R.id.usersBackButton).setOnClickListener { finish() }
        retryButton.setOnClickListener { loadUsers() }
        usersRecyclerView.layoutManager = LinearLayoutManager(this)

        loadUsers()
    }

    private fun loadUsers() {
        loadingProgress.visibility = View.VISIBLE
        errorText.visibility = View.GONE
        retryButton.visibility = View.GONE
        usersRecyclerView.visibility = View.GONE

        RetrofitClient.instance.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                loadingProgress.visibility = View.GONE
                val users = response.body()
                if (response.isSuccessful && users != null) {
                    usersRecyclerView.adapter = UserAdapter(users)
                    usersRecyclerView.visibility = View.VISIBLE
                    if (users.isEmpty()) {
                        errorText.text = "No hay usuarios registrados."
                        errorText.visibility = View.VISIBLE
                    }
                } else {
                    showError("No se pudo cargar la lista de usuarios.")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                loadingProgress.visibility = View.GONE
                showError("No se pudo conectar con el servidor.")
            }
        })
    }

    private fun showError(message: String) {
        errorText.text = message
        errorText.visibility = View.VISIBLE
        retryButton.visibility = View.VISIBLE
    }
}
