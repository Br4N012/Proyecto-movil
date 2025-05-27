package com.example.learncook

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.learncook.adaptadores.RecetaAdapter
import com.example.learncook.databinding.ActivityBuscarPresupuestoBinding
import com.example.learncook.interfaces.ListenerRecycleReceta
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.RecetaDatos

class BuscarPresupuestoActivity : AppCompatActivity(), ListenerRecycleReceta {
    private lateinit var binding: ActivityBuscarPresupuestoBinding
    private lateinit var db: LearnCookDB
    private var recetas = mutableListOf<RecetaDatos>()
    private lateinit var recetaAdapter: RecetaAdapter
    private var idUsuario = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuscarPresupuestoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = LearnCookDB(this)
        idUsuario = intent.getIntExtra("idUsuario", -1)

        binding.Buscar.setOnClickListener {
            val presupuestoMinimo = binding.edtPresupuestoMinimo.text.toString()
            val presupuestoMaximo = binding.edtPresupuestoMaximo.text.toString()
            if (validarDatos(presupuestoMinimo, presupuestoMaximo)) {
                configuracionRecycle()
                buscarRecetasPorPresupuesto(presupuestoMinimo, presupuestoMaximo)
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

    private fun configuracionRecycle() {
        binding.recyclerPresupuesto.layoutManager = LinearLayoutManager(this)
        binding.recyclerPresupuesto.setHasFixedSize(true)
    }

    private fun validarDatos(presupuestoMinimo: String, presupuestoMaximo: String): Boolean {
        if (presupuestoMinimo.isEmpty()) {
            binding.edtPresupuestoMinimo.error = "Llena este campo"
            return false
        }
        if (presupuestoMaximo.isEmpty()) {
            binding.edtPresupuestoMaximo.error = "Llena este campo"
            return false
        }

        val minimo: Double
        val maximo: Double

        try {
            minimo = presupuestoMinimo.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "El presupuesto mínimo no es un número válido", Toast.LENGTH_SHORT).show()
            return false
        }
        try {
            maximo = presupuestoMaximo.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "El presupuesto máximo no es un número válido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (minimo <= 0 || maximo <= 0) {
            Toast.makeText(this, "No puedes poner números negativos o menores a 1", Toast.LENGTH_SHORT).show()
            return false
        }
        if (maximo < minimo) {
            Toast.makeText(this, "El presupuesto máximo no debe ser menor al mínimo", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun buscarRecetasPorPresupuesto(presupuestoMinimo: String, presupuestoMaximo: String) {
        val minimo = presupuestoMinimo.toDouble()
        val maximo = presupuestoMaximo.toDouble()
        val recetasUnicas = mutableSetOf<RecetaDatos>()
        val unidadSeleccionada = recetas.firstOrNull()?.ingredientes?.firstOrNull()?.unidad ?: "g"
        val recetasEncontradas = db.buscarRecetasPorPresupuesto(minimo, maximo)
        recetasUnicas.addAll(recetasEncontradas)
        recetas.clear()
        recetas.addAll(recetasUnicas)

        if (recetas.isNotEmpty()) {
            for (receta in recetas) {
                receta.ingredientes = db.optenerLosIngredientesPorIdRecetas(receta.idReceta)
            }
            binding.recyclerPresupuesto.visibility = View.VISIBLE
            binding.tvNoHayRecetas.visibility = View.GONE
            recetaAdapter = RecetaAdapter(recetas, this@BuscarPresupuestoActivity, unidadSeleccionada)
            binding.recyclerPresupuesto.adapter = recetaAdapter
            recetaAdapter.notifyDataSetChanged()
        } else {
            binding.recyclerPresupuesto.visibility = View.GONE
            binding.tvNoHayRecetas.visibility = View.VISIBLE
        }
    }

    override fun clicEditarReceta(receta: RecetaDatos, position: Int) {
        Toast.makeText(this, "No puedes editar esta receta en la búsqueda por presupuesto", Toast.LENGTH_SHORT).show()
    }

    override fun clicEliminarReceta(receta: RecetaDatos, position: Int) {
        Toast.makeText(this, "No puedes eliminar esta receta en la búsqueda por presupuesto", Toast.LENGTH_SHORT).show()
    }

    override fun clicCalificarReceta(receta: RecetaDatos, position: Int) {
        val intent = Intent(this@BuscarPresupuestoActivity, CalificarRecetaActivity::class.java)
        intent.putExtra("idUsuario", idUsuario)
        intent.putExtra("idReceta", receta.idReceta)
        intent.putExtra("nombreReceta", receta.nombreReceta)
        startActivity(intent)
    }

    override fun clicCompartirReceta(receta: RecetaDatos, position: Int) {
        val mensaje = """
            Receta: ${receta.nombreReceta}
            Elaborada por: ${receta.nombreUsuario}
            Ingredientes: ${receta.ingredientes}
            Tiempo: ${receta.tiempo}
            Elaboración: ${receta.preparacion}
            Presupuesto: ${receta.presupuesto}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, mensaje)
        startActivity(Intent.createChooser(intent, "Compartir Receta!"))
    }
}