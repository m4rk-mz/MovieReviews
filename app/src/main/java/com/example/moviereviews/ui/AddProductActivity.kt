package com.example.moviereviews.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.moviereviews.R
import com.example.moviereviews.api.ApiService
import com.example.moviereviews.model.ProductCreateResponse
import com.example.moviereviews.model.ProductRequest
import com.example.moviereviews.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddProductActivity : AppCompatActivity() {

    private lateinit var api: ApiService
    private lateinit var titleInput: EditText
    private lateinit var priceInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var categoryInput: EditText
    private lateinit var imageInput: EditText
    private lateinit var progress: ProgressBar
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        // La pantalla no debe ser accesible para Cliente o Auditor.
        if (SessionManager(this).getRole() != "Administrador") {
            Toast.makeText(
                this,
                "Solo un administrador puede agregar productos",
                Toast.LENGTH_LONG
            ).show()
            startActivity(
                Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
            )
            finish()
            return
        }

        titleInput = findViewById(R.id.productTitleInput)
        priceInput = findViewById(R.id.productPriceInput)
        descriptionInput = findViewById(R.id.productDescriptionInput)
        categoryInput = findViewById(R.id.productCategoryInput)
        imageInput = findViewById(R.id.productImageInput)
        progress = findViewById(R.id.addProductProgress)
        saveButton = findViewById(R.id.saveProductButton)

        findViewById<Button>(R.id.cancelProductButton).setOnClickListener { finish() }
        saveButton.setOnClickListener { submitProduct() }

        api = Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private fun submitProduct() {
        clearFieldErrors()

        val title = titleInput.text.toString().trim()
        val priceText = priceInput.text.toString().trim()
        val price = priceText.toDoubleOrNull()
        val description = descriptionInput.text.toString().trim()
        val category = categoryInput.text.toString().trim()
        val image = imageInput.text.toString().trim()

        var hasErrors = false

        if (title.isBlank()) {
            titleInput.error = "Ingresa el título"
            hasErrors = true
        }

        if (priceText.isBlank()) {
            priceInput.error = "Ingresa el precio"
            hasErrors = true
        } else if (price == null || price <= 0.0) {
            priceInput.error = "Ingresa un precio numérico mayor que cero"
            hasErrors = true
        }

        if (description.isBlank()) {
            descriptionInput.error = "Ingresa la descripción"
            hasErrors = true
        }

        if (category.isBlank()) {
            categoryInput.error = "Ingresa la categoría"
            hasErrors = true
        }

        if (image.isBlank()) {
            imageInput.error = "Ingresa la URL de la imagen"
            hasErrors = true
        } else if (!Patterns.WEB_URL.matcher(image).matches()) {
            imageInput.error = "Ingresa una URL válida"
            hasErrors = true
        }

        // Las validaciones terminan aquí: no se hace ninguna petición HTTP si hay errores.
        if (hasErrors) {
            showMessage("Corrige los campos marcados")
            return
        }

        val product = ProductRequest(title, price!!, description, category, image)
        setLoading(true)

        api.createProduct(product).enqueue(object : Callback<ProductCreateResponse> {
            override fun onResponse(
                call: Call<ProductCreateResponse>,
                response: Response<ProductCreateResponse>
            ) {
                setLoading(false)
                val created = response.body()

                if (response.isSuccessful && created != null) {
                    // La historia solicita limpiar el formulario después de una creación exitosa.
                    clearForm()

                    AlertDialog.Builder(this@AddProductActivity)
                        .setTitle("Producto registrado")
                        .setMessage(
                            "El producto se agregó correctamente. ID asignado: ${created.id}"
                        )
                        .setPositiveButton("Aceptar") { _, _ ->
                            setResult(RESULT_OK)
                            finish()
                        }
                        .setCancelable(false)
                        .show()
                } else {
                    showMessage("No se pudo registrar el producto")
                }
            }

            override fun onFailure(call: Call<ProductCreateResponse>, t: Throwable) {
                setLoading(false)
                showMessage("Error de conexión. Intenta nuevamente")
            }
        })
    }

    private fun clearForm() {
        titleInput.text.clear()
        priceInput.text.clear()
        descriptionInput.text.clear()
        categoryInput.text.clear()
        imageInput.text.clear()
        clearFieldErrors()
    }

    private fun clearFieldErrors() {
        titleInput.error = null
        priceInput.error = null
        descriptionInput.error = null
        categoryInput.error = null
        imageInput.error = null
    }

    private fun setLoading(loading: Boolean) {
        progress.visibility = if (loading) View.VISIBLE else View.GONE
        saveButton.isEnabled = !loading
        saveButton.text = if (loading) "GUARDANDO..." else "GUARDAR PRODUCTO"
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
