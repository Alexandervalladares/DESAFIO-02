package com.example.ventaexpress.data

data class Venta(
    val id: String,
    val clienteId: String,
    val lineas: List<LineaVenta>,
    val total: Double,
    val fechaMs: Long
)
