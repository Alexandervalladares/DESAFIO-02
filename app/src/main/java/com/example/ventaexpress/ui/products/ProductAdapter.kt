package com.example.ventaexpress.ui.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ventaexpress.data.Producto
import com.example.ventaexpress.databinding.ItemProductBinding
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val onEdit: (Producto) -> Unit,
    private val onDelete: (Producto) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val items = mutableListOf<Producto>()
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

    fun submitList(data: List<Producto>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(producto: Producto) {
            binding.txtProductName.text = producto.nombre
            binding.txtProductPrice.text = currencyFormat.format(producto.precio)
            binding.txtProductStock.text = binding.root.context.getString(com.example.ventaexpress.R.string.product_stock, producto.stock)

            binding.btnEdit.setOnClickListener { onEdit(producto) }
            binding.btnDelete.setOnClickListener { onDelete(producto) }
        }
    }
}
