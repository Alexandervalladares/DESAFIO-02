package com.example.ventaexpress.ui.clients

import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.ventaexpress.R
import com.example.ventaexpress.data.Cliente
import com.example.ventaexpress.data.DataRepository
import com.example.ventaexpress.databinding.ActivityClientFormBinding

class ClientFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientFormBinding
    private var currentClient: Cliente? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val clientId = intent.getStringExtra(EXTRA_CLIENT_ID)
        currentClient = clientId?.let { DataRepository.getClienteById(it) }

        if (currentClient != null) {
            title = getString(R.string.edit)
            populateFields(currentClient!!)
        } else {
            title = getString(R.string.add_client)
        }

        binding.btnSaveClient.setOnClickListener {
            saveClient()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun populateFields(cliente: Cliente) {
        binding.inputClientName.setText(cliente.nombre)
        binding.inputClientEmail.setText(cliente.correo)
        binding.inputClientPhone.setText(cliente.telefono)
    }

    private fun saveClient() {
        val name = binding.inputClientName.text?.toString()?.trim().orEmpty()
        val email = binding.inputClientEmail.text?.toString()?.trim().orEmpty()
        val phone = binding.inputClientPhone.text?.toString()?.trim().orEmpty()

        var isValid = true

        if (name.isEmpty()) {
            binding.layoutClientName.error = getString(R.string.validation_client_name)
            isValid = false
        } else {
            binding.layoutClientName.error = null
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.layoutClientEmail.error = getString(R.string.validation_client_email)
            isValid = false
        } else {
            binding.layoutClientEmail.error = null
        }

        val digits = phone.filter { it.isDigit() }
        if (digits.length < 8) {
            binding.layoutClientPhone.error = getString(R.string.validation_client_phone)
            isValid = false
        } else {
            binding.layoutClientPhone.error = null
        }

        if (!isValid) {
            return
        }

        val client = currentClient?.copy(
            nombre = name,
            correo = email,
            telefono = phone
        ) ?: Cliente(
            id = DataRepository.generarId(),
            nombre = name,
            correo = email,
            telefono = phone
        )

        if (currentClient == null) {
            DataRepository.agregarCliente(client)
        } else {
            DataRepository.actualizarCliente(client)
        }

        finish()
    }

    companion object {
        const val EXTRA_CLIENT_ID = "extra_client_id"
    }
}
