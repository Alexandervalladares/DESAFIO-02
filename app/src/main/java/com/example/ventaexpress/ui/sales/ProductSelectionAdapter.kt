package com.example.ventaexpress.ui.sales

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ventaexpress.data.Producto
import com.example.ventaexpress.databinding.ItemProductSelectionBinding
import java.text.NumberFormat
import java.util.Locale

class ProductSelectionAdapter(
    private val onQuantityChanged: () -> Unit,
    private val onInvalidQuantity: (Producto) -> Unit
) : RecyclerView.Adapter<ProductSelectionAdapter.ProductSelectionViewHolder>() {

    private val items = mutableListOf<ProductSelectionItem>()
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

    fun submitList(data: List<Producto>) {
        items.clear()
        items.addAll(data.map { ProductSelectionItem(it, 0) })
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<ProductSelectionItem> = items.filter { it.cantidad > 0 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSelectionViewHolder {
        val binding = ItemProductSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductSelectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductSelectionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ProductSelectionViewHolder(private val binding: ItemProductSelectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductSelectionItem) {
            binding.txtSelectionName.text = item.producto.nombre
            binding.txtSelectionPrice.text = currencyFormat.format(item.producto.precio)
            binding.txtSelectionStock.text = binding.root.context.getString(com.example.ventaexpress.R.string.product_stock, item.producto.stock)
            binding.txtSelectionQuantity.text = item.cantidad.toString()

            binding.btnIncrease.setOnClickListener {
                updateQuantity(item, item.cantidad + 1)
            }
            binding.btnDecrease.setOnClickListener {
                updateQuantity(item, item.cantidad - 1)
            }
        }

        private fun updateQuantity(item: ProductSelectionItem, newQuantity: Int) {
            if (newQuantity < 0) return
            if (newQuantity > item.producto.stock) {
                onInvalidQuantity(item.producto)
                return
            }
            item.cantidad = newQuantity
            binding.txtSelectionQuantity.text = newQuantity.toString()
            onQuantityChanged()
        }
    }
}

data class ProductSelectionItem(
    val producto: Producto,
    var cantidad: Int
)
