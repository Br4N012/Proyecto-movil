package com.example.learncook.utilidades

import android.content.Context
import android.util.TypedValue
import android.widget.Button
import android.widget.TextView

object ConfiguracionUI {

    fun obtenerTamTexto(context: Context): Float {
        val prefs = context.getSharedPreferences("configuraciones", Context.MODE_PRIVATE)
        return prefs.getFloat("tam_texto", 16f)
    }

    fun obtenerTamBoton(context: Context): Float {
        val prefs = context.getSharedPreferences("configuraciones", Context.MODE_PRIVATE)
        return prefs.getFloat("tam_boton", 48f)
    }

    fun aplicarEstilos(context: Context, vararg views: Any) {
        val tamTexto = obtenerTamTexto(context)
        val tamBoton = obtenerTamBoton(context)

        views.forEach { view ->
            when (view) {
                is Button -> {
                    view.setTextSize(TypedValue.COMPLEX_UNIT_SP, tamTexto)
                    view.height = tamBoton.toInt()
                }
                is TextView -> {
                    view.setTextSize(TypedValue.COMPLEX_UNIT_SP, tamTexto)
                }
            }
        }
    }
}
