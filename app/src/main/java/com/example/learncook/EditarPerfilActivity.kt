package com.example.learncook

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.learncook.databinding.ActivityEditarPerfilBinding
import com.example.learncook.fragmentos.PerfilFragment
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Usuario

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var db: LearnCookDB
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = LearnCookDB(this)

        val idUsuario = intent.getIntExtra("idUsuario", -1)
        Log.d("EditarPerfilActivity", "idUsuario recibido: $idUsuario")
        usuario = db.traerUsuario2(idUsuario) ?: run {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.etNombreActual.setText(usuario.nombreUsuario)
        binding.etCorreoActual.setText(usuario.correo)
        binding.etContraseAActual.setText(usuario.contrasena)

        binding.btnGuardarCambios.setOnClickListener {
            val nombreNuevo = binding.etNuevoNombre.text.toString()
            val correoNuevo = binding.etNuevoCorreo.text.toString()
            val contrasenaNueva = binding.etNuevaContraseA.text.toString()

            if (nombreNuevo.isEmpty() || correoNuevo.isEmpty() || contrasenaNueva.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(correoNuevo).matches()) {
                Toast.makeText(this, "Por favor, ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (db.isCorreo(correoNuevo) && correoNuevo != usuario.correo) {
                Toast.makeText(this, "El correo electrónico ya está en uso", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (db.usuarioNombreRegistrado(nombreNuevo) && nombreNuevo != usuario.nombreUsuario) {
                Toast.makeText(this, "El nombre de usuario ya está en uso", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.actualizarNombreUsuario(usuario.id, nombreNuevo)
            db.actualizarContrasena(usuario.correo, contrasenaNueva)
            db.actualizarCorreo(usuario.correo, correoNuevo)

            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, PerfilFragment::class.java)
            intent.putExtra("idUsuario", idUsuario)
            startActivity(intent)

            finish()
        }

        binding.btnCancelarCambios.setOnClickListener {
            finish()
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

        val colorFondo = if (modoOscuro) Color.BLACK else Color.parseColor("#FCFFDF")
        val colorTexto = if (modoOscuro) Color.BLACK else Color.parseColor("#021D3D") // Cambia a negro en modo oscuro
        val colorBoton = if (modoOscuro) Color.DKGRAY else Color.parseColor("#4CAF50")

        binding.root.setBackgroundColor(colorFondo)

        ajustarTamaños(binding.root, tamTexto, tamBoton, colorTexto, colorBoton)
    }

    private fun ajustarTamaños(view: View, tamTexto: Float, tamBoton: Float, colorTexto: Int, colorBoton: Int) {
        when (view) {
            is TextView -> {
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, tamTexto)
                view.setTextColor(colorTexto)
            }
            is Button -> {
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, tamBoton)
                view.setBackgroundColor(colorBoton)
            }
            is EditText -> {
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, tamTexto)
                view.setTextColor(colorTexto)
            }
            is ViewGroup -> {
                for (i in 0 until view.childCount) {
                    ajustarTamaños(view.getChildAt(i), tamTexto, tamBoton, colorTexto, colorBoton)
                }
            }
        }
    }
}