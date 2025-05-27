package com.example.learncook

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.learncook.databinding.ActivitySeguirUsuarioBinding
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Usuario

class SeguirUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeguirUsuarioBinding
    private lateinit var modelo: LearnCookDB
    private var usuarioActual: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeguirUsuarioBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        modelo = LearnCookDB(this@SeguirUsuarioActivity)

        binding.btnBuscar.setOnClickListener {
            val nombreUsuario = binding.nombreUsuario.text.toString()
            if (nombreUsuario.isNotEmpty()) {
                val usuario = modelo.traerUsuarioPorNombre(nombreUsuario)
                if (usuario != null) {
                    binding.usernameTextView.text = usuario.nombreUsuario
                    binding.seguirButton.setOnClickListener {
                        seguirUsuario(usuario)
                    }
                } else {
                    Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor ingrese un nombre de usuario", Toast.LENGTH_SHORT).show()
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
        val tamBoton = prefs.getFloat("tam_boton", 45f) // Ajusta el tamaño del botón si es necesario
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

    private fun seguirUsuario(usuario: Usuario) {
        val idUsuarioActual = binding.nombreUsuario.text.toString()

        val seguidoExitosamente = modelo.seguirUsuario(idUsuarioActual, usuario.id)

        if (seguidoExitosamente) {
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Usuario Seguido")
                .setMessage("Ahora estás siguiendo a ${usuario.nombreUsuario}")
                .setPositiveButton("Aceptar") { dialog, which ->
                    // Regresar al home o a la actividad anterior
                    onBackPressed() // Simplemente regresamos al home
                }
                .create()
            alertDialog.show()
        } else {
            // Manejar caso de error al seguir al usuario
            Toast.makeText(this, "Error al seguir al usuario", Toast.LENGTH_SHORT).show()
        }
    }
}