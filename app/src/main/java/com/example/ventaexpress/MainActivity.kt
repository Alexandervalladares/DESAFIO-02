package com.example.ventaexpress

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ventaexpress.databinding.ActivityMainBinding
import com.example.ventaexpress.ui.clients.ClientsActivity
import com.example.ventaexpress.ui.products.ProductsActivity
import com.example.ventaexpress.ui.sales.SalesActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnProducts.setOnClickListener {
            startActivity(Intent(this, ProductsActivity::class.java))
        }

        binding.btnClients.setOnClickListener {
            startActivity(Intent(this, ClientsActivity::class.java))
        }

        binding.btnSales.setOnClickListener {
            startActivity(Intent(this, SalesActivity::class.java))
        }
    }
}
