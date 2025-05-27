// ToastHelper.kt en el paquete utilidades
package com.example.learncook.utilidades

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Vibrator
import android.os.VibrationEffect
import android.os.Build
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt

object ToastHelper {
    enum class ToastType {
        SUCCESS, ERROR, WARNING, INFO
    }

    // Configuración de colores (definidos directamente)
    @ColorInt private val SUCCESS_COLOR = Color.parseColor("#4CAF50")  // Verde
    @ColorInt private val ERROR_COLOR = Color.parseColor("#F44336")    // Rojo
    @ColorInt private val WARNING_COLOR = Color.parseColor("#FFC107")  // Amarillo
    @ColorInt private val INFO_COLOR = Color.parseColor("#2196F3")     // Azul
    @ColorInt private val TEXT_COLOR = Color.WHITE                     // Blanco

    // Configuración de vibración
    private const val VIBRATION_DURATION_MS = 20L  // Duración corta para feedback táctil

    /**
     * Muestra un toast con estilo y opcionalmente vibra
     * @param context Contexto de la aplicación
     * @param message Mensaje a mostrar
     * @param type Tipo de toast (success, error, etc.)
     * @param duration Duración del toast
     * @param withVibration Si se desea vibración al mostrar el toast
     */
    fun showToast(
        context: Context,
        message: String,
        type: ToastType = ToastType.SUCCESS,
        duration: Int = Toast.LENGTH_SHORT,
        withVibration: Boolean = true
    ) {
        // Configurar vibración si está habilitada
        if (withVibration) {
            vibrate(context, VIBRATION_DURATION_MS)
        }

        // Crear y configurar el toast
        val toast = Toast.makeText(context, "", duration)
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)

        // Crear vista personalizada
        val textView = TextView(context).apply {
            text = message
            setTextColor(TEXT_COLOR)
            textSize = 16f
            setPadding(60, 30, 60, 30)

            // Fondo con esquinas redondeadas
            background = GradientDrawable().apply {
                cornerRadius = 50f
                setColor(getColorForType(type))
            }
        }

        toast.view = textView
        toast.show()
    }

    /**
     * Hace vibrar el dispositivo
     * @param context Contexto de la aplicación
     * @param durationMs Duración de la vibración en milisegundos
     */
    fun vibrate(context: Context, durationMs: Long = VIBRATION_DURATION_MS) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(durationMs)
            }
        }
    }

    // Métodos abreviados para uso común
    fun showSuccess(context: Context, message: String, withVibration: Boolean = true) {
        showToast(context, message, ToastType.SUCCESS, Toast.LENGTH_SHORT, withVibration)
    }

    fun showError(context: Context, message: String, withVibration: Boolean = true) {
        showToast(context, message, ToastType.ERROR, Toast.LENGTH_LONG, withVibration)
    }

    fun showWarning(context: Context, message: String, withVibration: Boolean = true) {
        showToast(context, message, ToastType.WARNING, Toast.LENGTH_SHORT, withVibration)
    }

    fun showInfo(context: Context, message: String, withVibration: Boolean = true) {
        showToast(context, message, ToastType.INFO, Toast.LENGTH_SHORT, withVibration)
    }

    private fun getColorForType(type: ToastType): Int {
        return when (type) {
            ToastType.SUCCESS -> SUCCESS_COLOR
            ToastType.ERROR -> ERROR_COLOR
            ToastType.WARNING -> WARNING_COLOR
            ToastType.INFO -> INFO_COLOR
        }
    }
}