package com.example.learncook

import android.os.Bundle
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.learncook.adaptadores.CalificacionAdapter
import com.example.learncook.databinding.ActivityCalificarRecetaBinding
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Calificacion

class CalificarRecetaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalificarRecetaBinding
    private lateinit var modelo: LearnCookDB
    private var puntuacion = -1
    private var idUsuario = -1
    private var idReceta = -1
    private var nombreReceta = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalificarRecetaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modelo = LearnCookDB(this)
        idUsuario = intent.getIntExtra("idUsuario", -1)
        idReceta = intent.getIntExtra("idReceta", -1)
        nombreReceta = intent.getStringExtra("nombreReceta").toString()

        llenarCampos()
        mostrarCalificaciones()
        configuracionRecycle()

        binding.btnCalificar.setOnClickListener {
            if (validarDatos()) {
                insertarCalificacion()
            }
        }

        configurarEstrellas()

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
        val colorTexto = if (modoOscuro) Color.WHITE else Color.parseColor("#021D3D")
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

    private fun configurarEstrellas() {
        binding.ibEstrella1.setOnClickListener {
            pintarEstrellas(1)
            puntuacion = 1
        }
        binding.ibEstrella2.setOnClickListener {
            pintarEstrellas(2)
            puntuacion = 2
        }
        binding.ibEstrella3.setOnClickListener {
            pintarEstrellas(3)
            puntuacion = 3
        }
        binding.ibEstrella4.setOnClickListener {
            pintarEstrellas(4)
            puntuacion = 4
        }
        binding.ibEstrella5.setOnClickListener {
            pintarEstrellas(5)
            puntuacion = 5
        }
    }

    private fun pintarEstrellas(cantidad: Int) {
        val estrellas = listOf(
            binding.ibEstrella1,
            binding.ibEstrella2,
            binding.ibEstrella3,
            binding.ibEstrella4,
            binding.ibEstrella5
        )

        for (i in estrellas.indices) {
            estrellas[i].setImageResource(
                if (i < cantidad) R.mipmap.ic_estrella_amarilla else R.mipmap.ic_estrella_negra
            )
        }
    }

    private fun insertarCalificacion() {
        val comentario = binding.tiComentario.text.toString()
        val calificacion = Calificacion(0, idUsuario, idReceta, puntuacion, comentario)
        if (modelo.agregarCalificacion(calificacion) > 0) {
            Toast.makeText(this, "Calificación agregada correctamente", Toast.LENGTH_SHORT).show()
            mostrarCalificaciones()
        } else {
            Toast.makeText(this, "Ya calificaste esta receta", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validarDatos(): Boolean {
        return if (puntuacion < 0) {
            Toast.makeText(this, "Por favor elige una puntuación", Toast.LENGTH_SHORT).show()
            false
        } else if (binding.tiComentario.text.toString().isEmpty()) {
            binding.tiComentario.error = "Llena este campo."
            false
        } else {
            true
        }
    }

    private fun llenarCampos() {
        binding.tvNombreReceta.text = nombreReceta
        binding.tvNombreUsuario.text = modelo.traerNombreDeUsuario(idUsuario)
    }

    private fun mostrarCalificaciones() {
        val calificaciones = modelo.traerCalificacionesDeReceta(idReceta)

        if (calificaciones.isNotEmpty()) {
            binding.tvTextoNoHayComentarios.visibility = View.GONE
            binding.recycleComentarios.visibility = View.VISIBLE
            binding.recycleComentarios.adapter = CalificacionAdapter(calificaciones)
        } else {
            binding.tvTextoNoHayComentarios.visibility = View.VISIBLE
            binding.recycleComentarios.visibility = View.GONE
        }
    }

    private fun configuracionRecycle() {
        binding.recycleComentarios.layoutManager = LinearLayoutManager(this)
        binding.recycleComentarios.setHasFixedSize(true)
    }
}