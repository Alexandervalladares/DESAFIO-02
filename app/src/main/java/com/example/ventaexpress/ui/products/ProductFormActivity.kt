package com.example.ventaexpress.ui.products

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ventaexpress.R
import com.example.ventaexpress.data.DataRepository
import com.example.ventaexpress.data.Producto
import com.example.ventaexpress.databinding.ActivityProductFormBinding

class ProductFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductFormBinding
    private var currentProduct: Producto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val productId = intent.getStringExtra(EXTRA_PRODUCT_ID)
        currentProduct = productId?.let { DataRepository.getProductoById(it) }

        if (currentProduct != null) {
            title = getString(R.string.edit)
            populateFields(currentProduct!!)
        } else {
            title = getString(R.string.add_product)
        }

        binding.btnSaveProduct.setOnClickListener {
            saveProduct()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun populateFields(producto: Producto) {
        binding.inputProductName.setText(producto.nombre)
        binding.inputProductDescription.setText(producto.descripcion)
        binding.inputProductPrice.setText(producto.precio.toString())
        binding.inputProductStock.setText(producto.stock.toString())
    }

    private fun saveProduct() {
        val name = binding.inputProductName.text?.toString()?.trim().orEmpty()
        val description = binding.inputProductDescription.text?.toString()?.trim().orEmpty()
        val priceText = binding.inputProductPrice.text?.toString()?.trim()
        val stockText = binding.inputProductStock.text?.toString()?.trim()

        var isValid = true

        if (name.isEmpty()) {
            binding.layoutProductName.error = getString(R.string.validation_product_name)
            isValid = false
        } else {
            binding.layoutProductName.error = null
        }

        val price = priceText?.toDoubleOrNull()
        if (price == null || price <= 0.0) {
            binding.layoutProductPrice.error = getString(R.string.validation_product_price)
            isValid = false
        } else {
            binding.layoutProductPrice.error = null
        }

        val stock = stockText?.toIntOrNull()
        if (stock == null || stock < 0) {
            binding.layoutProductStock.error = getString(R.string.validation_product_stock)
            isValid = false
        } else {
            binding.layoutProductStock.error = null
        }

        if (!isValid || price == null || stock == null) {
            return
        }

        val product = currentProduct?.copy(
            nombre = name,
            descripcion = description,
            precio = price,
            stock = stock
        ) ?: Producto(
            id = DataRepository.generarId(),
            nombre = name,
            descripcion = description,
            precio = price,
            stock = stock
        )

        if (currentProduct == null) {
            DataRepository.agregarProducto(product)
        } else {
            DataRepository.actualizarProducto(product)
        }

        finish()
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
    }
}
