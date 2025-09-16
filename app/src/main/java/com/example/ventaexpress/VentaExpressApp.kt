package com.example.ventaexpress

import android.app.Application
import com.example.ventaexpress.data.DataRepository

class VentaExpressApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DataRepository.initialize(this)
    }
}
