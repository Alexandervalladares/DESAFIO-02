package com.example.ventaexpress.ui.sales

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ventaexpress.R
import com.example.ventaexpress.data.DataRepository
import com.example.ventaexpress.data.LineaVenta
import com.example.ventaexpress.databinding.ActivitySalesBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SalesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalesBinding
    private lateinit var adapter: SaleHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.sales_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = SaleHistoryAdapter()
        binding.recyclerSales.layoutManager = LinearLayoutManager(this)
        binding.recyclerSales.adapter = adapter

        binding.fabAddSale.setOnClickListener {
            startActivity(Intent(this, SaleFormActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshSales()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun refreshSales() {
        val sales = DataRepository.getVentas()
        val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        val items = sales.map { venta ->
            val cliente = DataRepository.getClienteById(venta.clienteId)
            val clientName = cliente?.nombre ?: getString(R.string.deleted_client)
            val totalText = numberFormat.format(venta.total)
            val dateText = dateFormat.format(Date(venta.fechaMs))

            val productsDetail = buildProductsDetail(venta.lineas)

            SaleHistoryItem(
                formattedDate = getString(R.string.sale_item_date, dateText),
                clientLabel = getString(R.string.sale_item_client, clientName),
                totalLabel = getString(R.string.sale_item_total, totalText),
                productsLabel = getString(R.string.sale_item_products, productsDetail)
            )
        }

        adapter.submitList(items)
        binding.txtEmptySales.isVisible = items.isEmpty()
    }

    private fun buildProductsDetail(lineas: List<LineaVenta>): String {
        if (lineas.isEmpty()) return ""
        return lineas.joinToString(separator = "\n") { linea ->
            val producto = DataRepository.getProductoById(linea.productoId)
            val productName = producto?.nombre ?: getString(R.string.deleted_product)
            "$productName x${linea.cantidad}"
        }
    }
}
