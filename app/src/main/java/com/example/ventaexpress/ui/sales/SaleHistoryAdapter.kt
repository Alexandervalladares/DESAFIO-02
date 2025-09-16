package com.example.ventaexpress.ui.sales

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ventaexpress.databinding.ItemSaleBinding

class SaleHistoryAdapter : RecyclerView.Adapter<SaleHistoryAdapter.SaleHistoryViewHolder>() {

    private val items = mutableListOf<SaleHistoryItem>()

    fun submitList(data: List<SaleHistoryItem>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleHistoryViewHolder {
        val binding = ItemSaleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SaleHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SaleHistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class SaleHistoryViewHolder(private val binding: ItemSaleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SaleHistoryItem) {
            binding.txtSaleDate.text = item.formattedDate
            binding.txtSaleClient.text = item.clientLabel
            binding.txtSaleTotal.text = item.totalLabel
            binding.txtSaleProducts.text = item.productsLabel
        }
    }
}

data class SaleHistoryItem(
    val formattedDate: String,
    val clientLabel: String,
    val totalLabel: String,
    val productsLabel: String
)
