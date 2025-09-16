package com.example.ventaexpress.data

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

object DataRepository {
    private const val PREFS_NAME = "ventaexpress_prefs"
    private const val KEY_PRODUCTS = "key_products"
    private const val KEY_CLIENTS = "key_clients"
    private const val KEY_SALES = "key_sales"

    private lateinit var preferences: SharedPreferences
    private val gson = Gson()

    private val productos = mutableListOf<Producto>()
    private val clientes = mutableListOf<Cliente>()
    private val ventas = mutableListOf<Venta>()

    private var initialized = false

    fun initialize(context: Context) {
        if (initialized) return
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadProductos()
        loadClientes()
        loadVentas()
        if (productos.isEmpty() || clientes.isEmpty()) {
            seedData()
            persistProductos()
            persistClientes()
        }
        initialized = true
    }

    fun getProductos(): List<Producto> = productos.sortedBy { it.nombre.lowercase() }

    fun getClientes(): List<Cliente> = clientes.sortedBy { it.nombre.lowercase() }

    fun getVentas(): List<Venta> = ventas.sortedByDescending { it.fechaMs }

    fun getProductoById(id: String): Producto? = productos.find { it.id == id }

    fun getClienteById(id: String): Cliente? = clientes.find { it.id == id }

    fun agregarProducto(producto: Producto) {
        productos.add(producto)
        persistProductos()
    }

    fun actualizarProducto(producto: Producto) {
        val index = productos.indexOfFirst { it.id == producto.id }
        if (index >= 0) {
            productos[index] = producto
            persistProductos()
        }
    }

    fun eliminarProducto(id: String): Boolean {
        val usadoEnVenta = ventas.any { venta ->
            venta.lineas.any { it.productoId == id }
        }
        if (usadoEnVenta) {
            return false
        }
        val removed = productos.removeIf { it.id == id }
        if (removed) {
            persistProductos()
        }
        return removed
    }

    fun agregarCliente(cliente: Cliente) {
        clientes.add(cliente)
        persistClientes()
    }

    fun actualizarCliente(cliente: Cliente) {
        val index = clientes.indexOfFirst { it.id == cliente.id }
        if (index >= 0) {
            clientes[index] = cliente
            persistClientes()
        }
    }

    fun eliminarCliente(id: String): Boolean {
        val usadoEnVenta = ventas.any { it.clienteId == id }
        if (usadoEnVenta) {
            return false
        }
        val removed = clientes.removeIf { it.id == id }
        if (removed) {
            persistClientes()
        }
        return removed
    }

    fun puedeRegistrarVenta(lineas: List<LineaVenta>): Boolean {
        if (lineas.isEmpty()) return false
        return lineas.all { linea ->
            val producto = productos.find { it.id == linea.productoId }
            producto != null && linea.cantidad <= producto.stock
        }
    }

    fun registrarVenta(venta: Venta): Boolean {
        if (!puedeRegistrarVenta(venta.lineas)) {
            return false
        }
        venta.lineas.forEach { linea ->
            val producto = productos.find { it.id == linea.productoId }
            if (producto != null) {
                producto.stock -= linea.cantidad
            }
        }
        ventas.add(venta)
        persistProductos()
        persistVentas()
        return true
    }

    fun generarId(): String = UUID.randomUUID().toString()

    private fun loadProductos() {
        val json = preferences.getString(KEY_PRODUCTS, null)
        if (!json.isNullOrEmpty()) {
            productos.clear()
            productos.addAll(fromJsonList<Producto>(json))
        }
    }

    private fun loadClientes() {
        val json = preferences.getString(KEY_CLIENTS, null)
        if (!json.isNullOrEmpty()) {
            clientes.clear()
            clientes.addAll(fromJsonList<Cliente>(json))
        }
    }

    private fun loadVentas() {
        val json = preferences.getString(KEY_SALES, null)
        if (!json.isNullOrEmpty()) {
            ventas.clear()
            ventas.addAll(fromJsonList<Venta>(json))
        }
    }

    private fun persistProductos() {
        preferences.edit().putString(KEY_PRODUCTS, gson.toJson(productos)).apply()
    }

    private fun persistClientes() {
        preferences.edit().putString(KEY_CLIENTS, gson.toJson(clientes)).apply()
    }

    private fun persistVentas() {
        preferences.edit().putString(KEY_SALES, gson.toJson(ventas)).apply()
    }

    private fun seedData() {
        if (productos.isEmpty()) {
            productos.addAll(
                listOf(
                    Producto(
                        id = generarId(),
                        nombre = "Laptop Gamer 15",
                        descripcion = "Portátil de alto rendimiento para juegos.",
                        precio = 999.99,
                        stock = 5
                    ),
                    Producto(
                        id = generarId(),
                        nombre = "Mouse Inalámbrico",
                        descripcion = "Mouse ergonómico con conexión inalámbrica.",
                        precio = 19.99,
                        stock = 25
                    ),
                    Producto(
                        id = generarId(),
                        nombre = "SSD NVMe 1TB",
                        descripcion = "Unidad de estado sólido NVMe de 1 TB.",
                        precio = 89.50,
                        stock = 10
                    )
                )
            )
        }

        if (clientes.isEmpty()) {
            clientes.addAll(
                listOf(
                    Cliente(
                        id = generarId(),
                        nombre = "María López",
                        correo = "maria@correo.com",
                        telefono = "77778888"
                    ),
                    Cliente(
                        id = generarId(),
                        nombre = "Carlos Pérez",
                        correo = "carlos@correo.com",
                        telefono = "70001122"
                    )
                )
            )
        }
    }

    private inline fun <reified T> fromJsonList(json: String): List<T> {
        val type = object : TypeToken<List<T>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    @VisibleForTesting
    fun clearAll() {
        productos.clear()
        clientes.clear()
        ventas.clear()
        preferences.edit().clear().apply()
        initialized = false
    }
}
