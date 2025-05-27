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
import com.example.learncook.databinding.ActivityBuscarIngredienteBinding
import com.example.learncook.interfaces.ListenerRecycleReceta
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Ingrediente
import com.example.learncook.poko.RecetaDatos

class BuscarIngredienteActivity : AppCompatActivity(), ListenerRecycleReceta {
    private lateinit var binding: ActivityBuscarIngredienteBinding
    private lateinit var learnCookDB: LearnCookDB
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listaDeIngredientes: List<Ingrediente>
    private lateinit var recetaAdapter: RecetaAdapter
    private var recetas = mutableListOf<RecetaDatos>()
    private lateinit var ingredienteSeleccionado: Ingrediente
    private var idIngrediente = mutableListOf<Int>()
    private var idUsuario = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuscarIngredienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        learnCookDB = LearnCookDB(this)
        idUsuario = intent.getIntExtra("idUsuario", -1)
        listaDeIngredientes = learnCookDB.traerIngredientes()

        adapter = ArrayAdapter(
            this,
            R.layout.spiner_item,
            listaDeIngredientes.map { it.nombre }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerIngredientes.adapter = adapter

        binding.spinnerIngredientes.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    if (position >= 1 && position < listaDeIngredientes.size) {
                        ingredienteSeleccionado = listaDeIngredientes[position]
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    binding.spinnerIngredientes.setSelection(0)
                }
            }

        binding.btnAgregar.setOnClickListener {
            agregarIngrediente(ingredienteSeleccionado)
        }
        // Debajo de agregarIngrediente() y antes de obtenerIngredientesSeleccionados()
        fun configuracionRecycle() {
            binding.recycleRecetas.layoutManager = LinearLayoutManager(this)
            binding.recycleRecetas.setHasFixedSize(true)
        }

        binding.btnBuscar.setOnClickListener {
            configuracionRecycle()
            val ingredientesSeleccionados = obtenerIngredientesSeleccionados()
            if (ingredientesSeleccionados.isNotEmpty()) {
                buscarRecetasPorIngredientes()
            } else {
                Toast.makeText(this, "Agregue al menos un ingrediente", Toast.LENGTH_SHORT).show()
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

    private fun agregarIngrediente(ingrediente: Ingrediente) {
        val nuevaFila = TableRow(this)
        val textView = TextView(this)
        textView.text = ingrediente.nombre
        textView.setTextColor(Color.WHITE)
        textView.setPadding(16, 16, 16, 16)
        nuevaFila.addView(textView)

        binding.tblIngredientes.addView(nuevaFila)
        idIngrediente.add(ingrediente.id)

        Toast.makeText(this, "Ingrediente agregado: ${ingrediente.nombre}", Toast.LENGTH_SHORT).show()
    }

    private fun obtenerIngredientesSeleccionados(): List<String> {
        val ingredientesSeleccionados = mutableListOf<String>()
        for (i in 0 until binding.tblIngredientes.childCount) {
            val fila = binding.tblIngredientes.getChildAt(i) as TableRow
            val textView = fila.getChildAt(0) as TextView
            ingredientesSeleccionados.add(textView.text.toString())
        }
        return ingredientesSeleccionados
    }

    private fun buscarRecetasPorIngredientes() {
        val recetasUnicas = mutableSetOf<RecetaDatos>()
        if (idIngrediente.isNotEmpty()) {
            for (id in idIngrediente) {
                val recetasEncontradas = learnCookDB.buscarRecetasPorIngredientes(id)
                recetasUnicas.addAll(recetasEncontradas)
            }

            recetas.clear()
            recetas.addAll(recetasUnicas)

            // Obtener ingredientes para cada receta
            recetas.forEach { receta ->
                receta.ingredientes = learnCookDB.optenerLosIngredientesPorIdRecetas(receta.idReceta)
            }

            if (recetas.isNotEmpty()) {
                val unidadSeleccionada = recetas.firstOrNull()?.ingredientes?.firstOrNull()?.unidad ?: "g"
                recetaAdapter = RecetaAdapter(recetas, this, unidadSeleccionada)
                binding.recycleRecetas.adapter = recetaAdapter
            } else {
                Toast.makeText(this, "No se encontraron recetas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun clicEditarReceta(receta: RecetaDatos, position: Int) {
        Toast.makeText(this, "No puedes hacer esto con esta receta", Toast.LENGTH_SHORT).show()
    }

    override fun clicEliminarReceta(receta: RecetaDatos, position: Int) {
        Toast.makeText(this, "No puedes hacer esto con esta receta", Toast.LENGTH_SHORT).show()
    }

    override fun clicCalificarReceta(receta: RecetaDatos, position: Int) {
        val intent = Intent(this@BuscarIngredienteActivity, CalificarRecetaActivity::class.java)
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