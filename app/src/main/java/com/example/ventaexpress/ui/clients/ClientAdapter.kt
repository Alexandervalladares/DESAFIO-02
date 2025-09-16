package com.example.ventaexpress.ui.clients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ventaexpress.data.Cliente
import com.example.ventaexpress.databinding.ItemClientBinding

class ClientAdapter(
    private val onEdit: (Cliente) -> Unit,
    private val onDelete: (Cliente) -> Unit
) : RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    private val items = mutableListOf<Cliente>()

    fun submitList(data: List<Cliente>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val binding = ItemClientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ClientViewHolder(private val binding: ItemClientBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cliente: Cliente) {
            binding.txtClientName.text = cliente.nombre
            binding.txtClientEmail.text = cliente.correo
            binding.txtClientPhone.text = cliente.telefono

            binding.btnEditClient.setOnClickListener { onEdit(cliente) }
            binding.btnDeleteClient.setOnClickListener { onDelete(cliente) }
        }
    }
}
