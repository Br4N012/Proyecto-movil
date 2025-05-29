package com.example.learncook

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.learncook.databinding.ActivityRegistroBinding
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Usuario
import com.example.learncook.utilidades.ToastHelper

class RegistroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroBinding
    private lateinit var modelo: LearnCookDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modelo = LearnCookDB(this)

        binding.btnRegistrarse.setOnClickListener {
            val correo = binding.etCorreo.text.toString()
            val contrasena = binding.etContrasena.text.toString()
            val nombreUsuario = binding.etNombreUsuario.text.toString()
            if (validarDatos(correo, contrasena, nombreUsuario)) {
                val usuario = Usuario(0, correo, contrasena, nombreUsuario)
                val registrado = modelo.agregarUsuario(usuario)
                if (registrado > 0) {
                    ToastHelper.showSuccess(this, "Usuario registrado correctamente")
                    finish()
                } else {
                    ToastHelper.showError(this, "No se pudo registrar el usuario")
                }
            }
        }

        aplicarPreferenciasVisuales()
    }

    override fun onResume() {
        super.onResume()
        aplicarPreferenciasVisuales()
    }

    private fun aplicarPreferenciasVisuales() {
        val prefs = getSharedPreferences("config_visual", MODE_PRIVATE)
        val tamTexto = prefs.getFloat("tam_texto", 20f)
        val tamBoton = prefs.getFloat("tam_boton", 20f)
        val modoOscuro = prefs.getBoolean("modo_oscuro", false)
        val colorTexto = if (modoOscuro) android.graphics.Color.parseColor("#ADD8E6") else android.graphics.Color.BLACK
        val colorFondo = if (modoOscuro) android.graphics.Color.BLACK else android.graphics.Color.parseColor("#FCFFDF")
        val colorBoton = if (modoOscuro) android.graphics.Color.GRAY else android.graphics.Color.parseColor("#4CAF50")

        binding.root.setBackgroundColor(colorFondo)
        ajustarTamaños(binding.root, tamTexto, tamBoton, colorTexto, colorBoton)
    }

    private fun ajustarTamaños(view: View, tamañoTexto: Float, tamañoBoton: Float, colorTexto: Int, colorBoton: Int) {
        when (view) {
            is TextView -> {
                view.textSize = tamañoTexto
                view.setTextColor(colorTexto)
            }
            is Button -> {
                view.textSize = tamañoBoton
                view.setTextColor(colorTexto)
                view.setBackgroundColor(colorBoton)
                val params = view.layoutParams
                params.height = (tamañoBoton * resources.displayMetrics.density).toInt()
                view.layoutParams = params
            }
            is EditText -> {
                view.textSize = tamañoTexto
                view.setTextColor(colorTexto)
            }
            is ViewGroup -> {
                for (i in 0 until view.childCount) {
                    ajustarTamaños(view.getChildAt(i), tamañoTexto, tamañoBoton, colorTexto, colorBoton)
                }
            }
        }
    }

    private fun validarDatos(correo: String, contrasena: String, nombreUsuario: String): Boolean {
        return when {
            correo.isEmpty() || contrasena.isEmpty() || nombreUsuario.isEmpty() -> {
                ToastHelper.showWarning(this, "Por favor llena todos los campos")
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
                ToastHelper.showError(this, "Correo no válido")
                false
            }
            contrasena.length < 8 -> {
                ToastHelper.showWarning(this, "La contraseña debe tener más de 8 caracteres")
                false
            }
            modelo.usuarioEnBase(correo) -> {
                ToastHelper.showError(this, "Este correo ya está registrado")
                false
            }
            modelo.usuarioNombreRegistrado(nombreUsuario) -> {
                ToastHelper.showWarning(this, "Este nombre de usuario ya está registrado")
                false
            }
            else -> true
        }
    }
}