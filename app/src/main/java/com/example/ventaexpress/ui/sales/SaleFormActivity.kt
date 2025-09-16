package com.example.ventaexpress.ui.sales

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ventaexpress.R
import com.example.ventaexpress.data.Cliente
import com.example.ventaexpress.data.DataRepository
import com.example.ventaexpress.data.LineaVenta
import com.example.ventaexpress.data.Venta
import com.example.ventaexpress.databinding.ActivitySaleFormBinding
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.Locale

class SaleFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaleFormBinding
    private lateinit var adapter: ProductSelectionAdapter
    private val clients = mutableListOf<Cliente>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.new_sale_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecycler()
        loadClients()
        loadProducts()

        binding.btnSaveSale.setOnClickListener {
            saveSale()
        }

        updateTotal()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupRecycler() {
        adapter = ProductSelectionAdapter(
            onQuantityChanged = { updateTotal() },
            onInvalidQuantity = { producto ->
                Snackbar.make(binding.root, getString(R.string.sale_error_stock, producto.nombre), Snackbar.LENGTH_LONG).show()
            }
        )
        binding.recyclerProductSelection.layoutManager = LinearLayoutManager(this)
        binding.recyclerProductSelection.adapter = adapter
    }

    private fun loadClients() {
        clients.clear()
        clients.addAll(DataRepository.getClientes())
        val entries = mutableListOf(getString(R.string.select_client))
        entries.addAll(clients.map { it.nombre })
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, entries)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerClients.adapter = spinnerAdapter
    }

    private fun loadProducts() {
        val products = DataRepository.getProductos()
        adapter.submitList(products)
        binding.recyclerProductSelection.isVisible = products.isNotEmpty()
        binding.txtProductsTitle.isVisible = products.isNotEmpty()
        if (products.isEmpty()) {
            Snackbar.make(binding.root, R.string.empty_products, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun saveSale() {
        if (clients.isEmpty()) {
            Snackbar.make(binding.root, R.string.validation_sale_client, Snackbar.LENGTH_LONG).show()
            return
        }

        val selectedIndex = binding.spinnerClients.selectedItemPosition
        if (selectedIndex <= 0) {
            Snackbar.make(binding.root, R.string.validation_sale_client, Snackbar.LENGTH_LONG).show()
            return
        }
        val cliente = clients[selectedIndex - 1]

        val selectedItems = adapter.getSelectedItems()
        if (selectedItems.isEmpty()) {
            Snackbar.make(binding.root, R.string.validation_sale_products, Snackbar.LENGTH_LONG).show()
            return
        }

        val lineas = selectedItems.map { item ->
            LineaVenta(
                productoId = item.producto.id,
                cantidad = item.cantidad,
                precioUnitario = item.producto.precio
            )
        }

        if (!DataRepository.puedeRegistrarVenta(lineas)) {
            Snackbar.make(binding.root, R.string.validation_sale_stock, Snackbar.LENGTH_LONG).show()
            return
        }

        val total = lineas.sumOf { it.cantidad * it.precioUnitario }
        val venta = Venta(
            id = DataRepository.generarId(),
            clienteId = cliente.id,
            lineas = lineas,
            total = total,
            fechaMs = System.currentTimeMillis()
        )

        val registrado = DataRepository.registrarVenta(venta)
        if (registrado) {
            Snackbar.make(binding.root, R.string.sale_success, Snackbar.LENGTH_SHORT).show()
            finish()
        } else {
            Snackbar.make(binding.root, R.string.validation_sale_stock, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun updateTotal() {
        val total = adapter.getSelectedItems().sumOf { it.cantidad * it.producto.precio }
        val formatted = NumberFormat.getCurrencyInstance(Locale.getDefault()).format(total)
        binding.txtTotalAmount.text = getString(R.string.total_amount, formatted)
    }
}
