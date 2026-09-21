package com.example.moviereviews.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.moviereviews.R
import com.example.moviereviews.api.ApiService
import com.example.moviereviews.model.Product
import com.example.moviereviews.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var api: ApiService
    private lateinit var detailProgress: ProgressBar
    private lateinit var detailContent: LinearLayout
    private lateinit var detailImage: ImageView
    private lateinit var detailTitle: TextView
    private lateinit var detailPrice: TextView
    private lateinit var detailCategory: TextView
    private lateinit var detailDescription: TextView
    private lateinit var detailRating: TextView
    private lateinit var adminActionsContainer: LinearLayout

    private var currentProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_product_detail)

        findViewById<Button>(R.id.backButton)
            .setOnClickListener {
                finish()
            }

        detailProgress =
            findViewById(R.id.detailProgress)

        detailContent =
            findViewById(R.id.detailContent)

        detailImage =
            findViewById(R.id.detailImage)

        detailTitle =
            findViewById(R.id.detailTitle)

        detailPrice =
            findViewById(R.id.detailPrice)

        detailCategory =
            findViewById(R.id.detailCategory)

        detailDescription =
            findViewById(R.id.detailDescription)

        detailRating =
            findViewById(R.id.detailRating)

        adminActionsContainer =
            findViewById(R.id.adminActionsContainer)

        val retrofit =
            Retrofit.Builder()
                .baseUrl("https://fakestoreapi.com/")
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()

        api =
            retrofit.create(ApiService::class.java)

        val productId =
            intent.getIntExtra(
                "productId",
                -1
            )

        if (productId == -1) {

            showUnavailableAndReturn()

        } else {

            loadProduct(productId)
        }
    }

    // -------------------------
    // CARGAR DETALLE
    // -------------------------

    private fun loadProduct(productId: Int) {

        detailProgress.visibility =
            View.VISIBLE

        detailContent.visibility =
            View.GONE

        api.getProduct(productId)
            .enqueue(
                object : Callback<Product> {

                    override fun onResponse(
                        call: Call<Product>,
                        response: Response<Product>
                    ) {

                        val product =
                            response.body()

                        if (
                            response.isSuccessful &&
                            product != null
                        ) {

                            currentProduct = product
                            showProduct(product)

                        } else {

                            showUnavailableAndReturn()
                        }
                    }

                    override fun onFailure(
                        call: Call<Product>,
                        t: Throwable
                    ) {

                        showUnavailableAndReturn()
                    }
                }
            )
    }

    // -------------------------
    // MOSTRAR PRODUCTO
    // -------------------------

    private fun showProduct(product: Product) {

        detailProgress.visibility =
            View.GONE

        detailContent.visibility =
            View.VISIBLE

        detailTitle.text =
            product.title

        detailPrice.text =
            "$" + String.format(
                "%.2f",
                product.price
            )

        detailCategory.text =
            "Categoría: " + product.category

        detailDescription.text =
            product.description

        detailRating.text =
            "Calificación: " +
                    product.rating.rate +
                    " (" +
                    product.rating.count +
                    " opiniones)"

        Glide.with(this)
            .load(product.image)
            .into(detailImage)

        createAdminActions(product)
    }

    // -------------------------
    // ACCIONES DE ADMINISTRADOR
    // -------------------------

    private fun createAdminActions(
        product: Product
    ) {

        adminActionsContainer.removeAllViews()

        val role =
            SessionManager(this).getRole()

        if (role != "Administrador") {
            return
        }

        val editButton =
            Button(this).apply {
                text = "EDITAR"
                setTextColor(
                    getColor(R.color.white)
                )
                setBackgroundResource(
                    R.drawable.button_background
                )
                backgroundTintList = null
                setOnClickListener {
                    showEditDialog(product)
                }
            }

        val deleteButton =
            Button(this).apply {
                text = "ELIMINAR"
                setTextColor(
                    getColor(R.color.white)
                )
                backgroundTintList =
                    ColorStateList.valueOf(
                        getColor(R.color.error_red)
                    )
                setOnClickListener {
                    confirmDelete(product)
                }
            }

        val editParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            )

        val deleteParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            ).apply {
                topMargin = dp(10)
            }

        adminActionsContainer.addView(
            editButton,
            editParams
        )

        adminActionsContainer.addView(
            deleteButton,
            deleteParams
        )
    }

    // -------------------------
    // EDITAR PRODUCTO
    // -------------------------

    private fun showEditDialog(
        product: Product
    ) {

        val form =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    dp(20),
                    dp(8),
                    dp(20),
                    0
                )
            }

        val titleInput =
            EditText(this).apply {
                hint = "Título"
                setText(product.title)
            }

        val priceInput =
            EditText(this).apply {
                hint = "Precio"
                inputType =
                    InputType.TYPE_CLASS_NUMBER or
                            InputType.TYPE_NUMBER_FLAG_DECIMAL

                setText(
                    product.price.toString()
                )
            }

        val descriptionInput =
            EditText(this).apply {
                hint = "Descripción"
                setText(product.description)
            }

        val categoryInput =
            EditText(this).apply {
                hint = "Categoría"
                setText(product.category)
            }

        form.addView(titleInput)
        form.addView(priceInput)
        form.addView(descriptionInput)
        form.addView(categoryInput)

        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Editar producto")
                .setView(form)
                .setNegativeButton(
                    "Cancelar",
                    null
                )
                .setPositiveButton(
                    "Guardar",
                    null
                )
                .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val price =
                    priceInput.text
                        .toString()
                        .toDoubleOrNull()

                if (
                    titleInput.text.isBlank() ||
                    price == null ||
                    descriptionInput.text.isBlank() ||
                    categoryInput.text.isBlank()
                ) {

                    Toast.makeText(
                        this,
                        "Completa todos los datos",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                val updatedProduct =
                    product.copy(
                        title =
                            titleInput.text
                                .toString()
                                .trim(),
                        price = price,
                        description =
                            descriptionInput.text
                                .toString()
                                .trim(),
                        category =
                            categoryInput.text
                                .toString()
                                .trim()
                    )

                updateProduct(
                    updatedProduct,
                    dialog
                )
            }
        }

        dialog.show()
    }

    private fun updateProduct(
        product: Product,
        dialog: AlertDialog
    ) {

        api.updateProduct(
            product.id,
            product
        ).enqueue(
            object : Callback<Product> {

                override fun onResponse(
                    call: Call<Product>,
                    response: Response<Product>
                ) {

                    if (response.isSuccessful) {

                        currentProduct = product
                        showProduct(product)
                        dialog.dismiss()

                        Toast.makeText(
                            this@ProductDetailActivity,
                            "Producto actualizado",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        showOperationError()
                    }
                }

                override fun onFailure(
                    call: Call<Product>,
                    t: Throwable
                ) {

                    showOperationError()
                }
            }
        )
    }

    // -------------------------
    // ELIMINAR PRODUCTO
    // -------------------------

    private fun confirmDelete(
        product: Product
    ) {

        AlertDialog.Builder(this)
            .setTitle("Eliminar producto")
            .setMessage(
                "¿Deseas eliminar este producto?"
            )
            .setNegativeButton(
                "Cancelar",
                null
            )
            .setPositiveButton(
                "Eliminar"
            ) { _, _ ->

                deleteProduct(product.id)
            }
            .show()
    }

    private fun deleteProduct(productId: Int) {

        api.deleteProduct(productId)
            .enqueue(
                object : Callback<Product> {

                    override fun onResponse(
                        call: Call<Product>,
                        response: Response<Product>
                    ) {

                        if (response.isSuccessful) {

                            Toast.makeText(
                                this@ProductDetailActivity,
                                "Producto eliminado",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()

                        } else {

                            showOperationError()
                        }
                    }

                    override fun onFailure(
                        call: Call<Product>,
                        t: Throwable
                    ) {

                        showOperationError()
                    }
                }
            )
    }

    private fun showOperationError() {

        Toast.makeText(
            this,
            "No se pudo realizar la operación",
            Toast.LENGTH_SHORT
        ).show()
    }

    // -------------------------
    // PRODUCTO NO DISPONIBLE
    // -------------------------

    private fun showUnavailableAndReturn() {

        detailProgress.visibility =
            View.GONE

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "Producto no disponible"
                )
                .setMessage(
                    "Regresando al catálogo"
                )
                .setCancelable(false)
                .create()

        dialog.setOnDismissListener {
            finish()
        }

        dialog.show()

        Handler(
            Looper.getMainLooper()
        ).postDelayed(
            {
                if (!isFinishing) {
                    dialog.dismiss()
                }
            },
            1500
        )
    }

    private fun dp(value: Int): Int {

        return (
                value *
                        resources.displayMetrics.density
                ).toInt()
    }
}