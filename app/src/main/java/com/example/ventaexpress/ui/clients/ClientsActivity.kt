package com.example.ventaexpress.ui.clients

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ventaexpress.R
import com.example.ventaexpress.data.Cliente
import com.example.ventaexpress.data.DataRepository
import com.example.ventaexpress.databinding.ActivityClientsBinding
import com.google.android.material.snackbar.Snackbar

class ClientsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientsBinding
    private lateinit var adapter: ClientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.clients_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = ClientAdapter(onEdit = { cliente ->
            openForm(cliente)
        }, onDelete = { cliente ->
            confirmDelete(cliente)
        })

        binding.recyclerClients.layoutManager = LinearLayoutManager(this)
        binding.recyclerClients.adapter = adapter

        binding.fabAddClient.setOnClickListener {
            openForm(null)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshClients()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun openForm(cliente: Cliente?) {
        val intent = Intent(this, ClientFormActivity::class.java)
        if (cliente != null) {
            intent.putExtra(ClientFormActivity.EXTRA_CLIENT_ID, cliente.id)
        }
        startActivity(intent)
    }

    private fun refreshClients() {
        val clients = DataRepository.getClientes()
        adapter.submitList(clients)
        binding.txtEmptyClients.isVisible = clients.isEmpty()
    }

    private fun confirmDelete(cliente: Cliente) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete)
            .setMessage(R.string.confirm_delete_client)
            .setPositiveButton(R.string.confirm) { _, _ ->
                deleteClient(cliente)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun deleteClient(cliente: Cliente) {
        val removed = DataRepository.eliminarCliente(cliente.id)
        if (removed) {
            refreshClients()
        } else {
            Snackbar.make(binding.root, R.string.error_delete_client_with_sales, Snackbar.LENGTH_LONG).show()
        }
    }
}
