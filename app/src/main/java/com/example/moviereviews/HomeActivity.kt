package com.example.moviereviews

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        // Elementos de la interfaz
        val welcomeText =
            findViewById<TextView>(R.id.welcomeText)

        val usernameText =
            findViewById<TextView>(R.id.usernameText)

        val passwordText =
            findViewById<TextView>(R.id.passwordText)

        val roleText =
            findViewById<TextView>(R.id.roleText)

        val apiStatusText =
            findViewById<TextView>(R.id.apiStatusText)

        val apiCard =
            findViewById<LinearLayout>(R.id.apiCard)

        val accountCard =
            findViewById<LinearLayout>(R.id.accountCard)

        val logoutButton =
            findViewById<Button>(R.id.logoutButton)

        // Sesión
        val sessionManager =
            SessionManager(this)

        val role =
            sessionManager.getRole()

        // Recibir usuario y contraseña
        val username =
            intent.getStringExtra("username")
                ?: "Usuario"

        val password =
            intent.getStringExtra("password")
                ?: ""

        // Mostrar información
        welcomeText.text =
            "Bienvenido, $username"

        usernameText.text =
            username

        roleText.text =
            role ?: "Administrador"

        // Mostrar contraseña oculta
        if (password.isNotEmpty()) {

            passwordText.text =
                "•".repeat(password.length)

        } else {

            passwordText.text =
                "••••••••"
        }

        // Estado visual de Fake Store API
        apiStatusText.text =
            "●  Conectado"

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

        // Retrasos
        slideUpApi.startOffset = 200

        slideUpAccount.startOffset = 450

        fadeButton.startOffset = 700

        // Ejecutar animaciones
        welcomeText.startAnimation(
            fadeIn
        )

        apiCard.startAnimation(
            slideUpApi
        )

        accountCard.startAnimation(
            slideUpAccount
        )

        logoutButton.startAnimation(
            fadeButton
        )

        // -------------------------
        // CERRAR SESIÓN
        // -------------------------

        logoutButton.setOnClickListener {

            // Eliminar sesión
            sessionManager.clearSession()

            // Regresar al Login
            val intent =
                Intent(
                    this,
                    MainActivity::class.java
                )

            // Limpiar historial de Activities
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            // Transición hacia Login
            overridePendingTransition(
                R.anim.fade_in,
                R.anim.fade_out
            )

            finish()
        }
    }
}