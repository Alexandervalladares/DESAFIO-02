package com.example.ventaexpress.data

data class Producto(
    val id: String,
    var nombre: String,
    var descripcion: String,
    var precio: Double,
    var stock: Int
)
