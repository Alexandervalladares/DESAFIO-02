package com.example.ventaexpress.ui.products

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ventaexpress.R
import com.example.ventaexpress.data.DataRepository
import com.example.ventaexpress.data.Producto
import com.example.ventaexpress.databinding.ActivityProductsBinding
import com.google.android.material.snackbar.Snackbar

class ProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductsBinding
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.products_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = ProductAdapter(onEdit = { producto ->
            openForm(producto)
        }, onDelete = { producto ->
            confirmDelete(producto)
        })

        binding.recyclerProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerProducts.adapter = adapter

        binding.fabAddProduct.setOnClickListener {
            openForm(null)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshProducts()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun openForm(producto: Producto?) {
        val intent = Intent(this, ProductFormActivity::class.java)
        if (producto != null) {
            intent.putExtra(ProductFormActivity.EXTRA_PRODUCT_ID, producto.id)
        }
        startActivity(intent)
    }

    private fun refreshProducts() {
        val products = DataRepository.getProductos()
        adapter.submitList(products)
        binding.txtEmptyProducts.isVisible = products.isEmpty()
    }

    private fun confirmDelete(producto: Producto) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete)
            .setMessage(R.string.confirm_delete_product)
            .setPositiveButton(R.string.confirm) { _, _ ->
                deleteProduct(producto)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun deleteProduct(producto: Producto) {
        val removed = DataRepository.eliminarProducto(producto.id)
        if (removed) {
            refreshProducts()
        } else {
            Snackbar.make(binding.root, R.string.error_delete_product_with_sales, Snackbar.LENGTH_LONG).show()
        }
    }
}
