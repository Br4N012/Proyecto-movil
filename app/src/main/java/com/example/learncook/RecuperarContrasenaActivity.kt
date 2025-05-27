package com.example.learncook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.learncook.databinding.ActivityRecuperarContrasenaBinding
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.utilidades.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class RecuperarContrasenaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperarContrasenaBinding
    private val emailUtil = Email()
    private lateinit var progressBar: ProgressBar
    private var codigo: Int = 0
    private var correo: String = ""
    private lateinit var modelo: LearnCookDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecuperarContrasenaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modelo = LearnCookDB(this)
        progressBar = binding.progressBar

        binding.btnEnviarCorreo.setOnClickListener {
            correo = binding.etCorreo.text.toString()
            if (validarDatosCorreo(correo)) {
                if (modelo.isCorreo(correo)) {
                    enviarCorreo(correo)
                } else {
                    Toast.makeText(this, "Este correo no se encuentra registrado", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnEnviarCodigo.setOnClickListener {
            if (validarDatosCodigo()) {
                val codigoDeEntrada = binding.etCodigo.text.toString().toIntOrNull()
                if (codigoDeEntrada != null && esIgualElCodigo(codigo, codigoDeEntrada)) {
                    binding.etCodigo.visibility = View.GONE
                    binding.btnEnviarCodigo.visibility = View.GONE
                    binding.etContrasena.visibility = View.VISIBLE
                    binding.btnRestaurarContrasena.visibility = View.VISIBLE
                    Toast.makeText(this, "Escribe una nueva contraseña", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Error: el código no es correcto", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnRestaurarContrasena.setOnClickListener {
            val contrasena = binding.etContrasena.text.toString()
            if (validarDatosContrasena(contrasena)) {
                val actualizado = modelo.actualizarContrasena(correo, contrasena)
                if (actualizado > 0) {
                    Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "No se pudo actualizar la contraseña", Toast.LENGTH_SHORT).show()
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

    private fun validarDatosContrasena(contrasena: String): Boolean {
        return when {
            contrasena.isEmpty() -> {
                binding.etContrasena.error = "Favor de llenar este campo"
                false
            }
            contrasena.length < 8 -> {
                binding.etContrasena.error = "La contraseña debe ser más de 8 caracteres"
                false
            }
            else -> true
        }
    }

    private fun validarDatosCorreo(correo: String): Boolean {
        return when {
            correo.isBlank() || !correo.contains("@") -> {
                binding.etCorreo.error = "Favor de ingresar un correo válido"
                false
            }
            else -> true
        }
    }

    private fun enviarCorreo(correo: String) {
        val subject = "Código de Recuperación"
        codigo = generarCodigo()
        val body = "Tu código de recuperación es: $codigo"

        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            val exitoEnvio = withContext(Dispatchers.IO) {
                emailUtil.sendEmail(correo, subject, body)
            }

            progressBar.visibility = View.GONE

            if (exitoEnvio) {
                binding.etCorreo.visibility = View.GONE
                binding.btnEnviarCorreo.visibility = View.GONE
                binding.etCodigo.visibility = View.VISIBLE
                binding.btnEnviarCodigo.visibility = View.VISIBLE
                Toast.makeText(this@RecuperarContrasenaActivity, "Se envió un código a tu correo electrónico", Toast.LENGTH_LONG).show()
            } else {
                Log.e("Envio", "Error al enviar el correo electrónico")
                Toast.makeText(this@RecuperarContrasenaActivity, "Error al enviar el correo", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun generarCodigo(): Int {
        return Random.nextInt(1000, 10000)
    }

    private fun validarDatosCodigo(): Boolean {
        val codigo = binding.etCodigo.text.toString()
        return when {
            codigo.length != 4 || !codigo.all { it.isDigit() } -> {
                binding.etCodigo.error = "El código debe tener 4 dígitos"
                false
            }
            else -> true
        }
    }

    private fun esIgualElCodigo(codigo: Int, codigoDeEntrada: Int): Boolean {
        return codigo == codigoDeEntrada
    }
}