package com.example.learncook

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.learncook.databinding.ActivityLoginBinding
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Usuario

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var modelo: LearnCookDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modelo = LearnCookDB(this)

        binding.btnIngreso.setOnClickListener {
            val correo = binding.etCorreo.text.toString()
            val contrasena = binding.etContrasena.text.toString()
            if (validarCampos(correo, contrasena)) {
                var usuario = Usuario(-1, correo, contrasena, "")
                if (modelo.usuarioRegistrado(usuario)) {
                    usuario = modelo.traerUsuario(usuario)!!
                    Toast.makeText(this, "Bienvenido ${usuario.nombreUsuario}", Toast.LENGTH_SHORT).show()
                    irPantallaHome(usuario.id)
                } else {
                    Toast.makeText(this, "Usuario o Contraseña Incorrecto", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.twRecuperarContrasena.setOnClickListener {
            val intent = Intent(this, RecuperarContrasenaActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegistrarse.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
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

        binding.root.setBackgroundColor(colorFondo)

        ajustarTamaños(binding.root, tamTexto, tamBoton, colorTexto, modoOscuro) // Pasar modoOscuro
    }

    private fun ajustarTamaños(view: View, tamañoTexto: Float, tamañoBoton: Float, colorTexto: Int, modoOscuro: Boolean) {
        val colorBoton = if (modoOscuro) android.graphics.Color.GRAY else android.graphics.Color.parseColor("#4CAF50") // Gris para modo oscuro, verde por defecto
        when (view) {
            is TextView -> {
                view.textSize = tamañoTexto
                view.setTextColor(colorTexto)
            }
            is Button -> {
                view.textSize = tamañoBoton
                view.setTextColor(colorTexto)
                view.setBackgroundColor(colorBoton) // Establecer el color de fondo del botón
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
                    ajustarTamaños(view.getChildAt(i), tamañoTexto, tamañoBoton, colorTexto, modoOscuro)
                }
            }
        }
    }

    private fun validarCampos(correo: String, contrasena: String): Boolean {
        return when {
            correo.isEmpty() || contrasena.isEmpty() -> {
                if (correo.isEmpty()) binding.etCorreo.error = "Favor de llenar este campo!"
                if (contrasena.isEmpty()) binding.etContrasena.error = "Favor de llenar este campo!"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
                binding.etCorreo.error = "Favor de ingresar un correo electrónico válido!"
                false
            }
            contrasena.length < 8 -> {
                binding.etContrasena.error = "La contraseña debe tener al menos 8 caracteres!"
                false
            }
            else -> true
        }
    }

    private fun irPantallaHome(idUsuario: Int) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("idUsuario", idUsuario)
        startActivity(intent)
        finish()
    }
}