package com.example.learncook.fragmentos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.learncook.AgregarRecetaActivity
import com.example.learncook.CalificarRecetaActivity
import com.example.learncook.EditarRecetaActivity
import com.example.learncook.adaptadores.RecetaAdapter
import com.example.learncook.databinding.FragmentRecetaBinding
import com.example.learncook.interfaces.ListenerRecycleReceta
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.RecetaDatos

private const val ARG_ID_USUARIO = "idUsuario"

class RecetaFragment : Fragment(), ListenerRecycleReceta {
    private lateinit var binding: FragmentRecetaBinding
    private var idUsuario: Int = -1
    private lateinit var modelo: LearnCookDB
    private lateinit var recetaAdapter: RecetaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idUsuario = it.getInt(ARG_ID_USUARIO)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRecetaBinding.inflate(inflater, container, false)
        modelo = LearnCookDB(requireContext()) // Inicialización del modelo
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        aplicarPreferenciasVisuales()

        // Restaurar el botón "Agregar Receta" para abrir la actividad de agregar receta
        binding.btnAgregarReceta.setOnClickListener {
            val intent = Intent(requireContext(), AgregarRecetaActivity::class.java)
            intent.putExtra(ARG_ID_USUARIO, idUsuario)
            startActivity(intent)
        }

        // Configurar el botón "Modo Lectura" para ocultar elementos innecesarios
        binding.btnModoLectura.setOnClickListener {
            activarModoLectura()
        }

        binding.btnSalirModoLectura.setOnClickListener {
            desactivarModoLectura()
        }

        cargarMisRecetas()
        configuracionRecycle()
    }

    override fun onResume() {
        super.onResume()
        aplicarPreferenciasVisuales()
        cargarMisRecetas()
    }

    private fun aplicarPreferenciasVisuales() {
        val prefs = requireContext().getSharedPreferences("config_visual", Context.MODE_PRIVATE)
        val tamTexto = prefs.getFloat("tam_texto", 20f)
        val modoOscuro = prefs.getBoolean("modo_oscuro", false)

        val colorFondo = if (modoOscuro) android.graphics.Color.BLACK else android.graphics.Color.parseColor("#FCFFDF")
        val colorTexto = if (modoOscuro) android.graphics.Color.BLACK else android.graphics.Color.parseColor("#021D3D")

        binding.root.setBackgroundColor(colorFondo)
        binding.tvMensajeRecetas.setTextSize(TypedValue.COMPLEX_UNIT_SP, tamTexto)
        binding.tvMensajeRecetas.setTextColor(colorTexto)
    }

    private fun activarModoLectura() {
        binding.btnAgregarReceta.visibility = View.GONE
        binding.tvMensajeRecetas.visibility = View.GONE // Oculta el título "Tus Recetas"
        binding.recycleRecetas.visibility = View.VISIBLE // Solo muestra las recetas

        binding.btnModoLectura.visibility = View.GONE
        binding.btnSalirModoLectura.visibility = View.VISIBLE

        // Activar pantalla completa ocultando barras de estado y navegación
        requireActivity().window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        Toast.makeText(requireContext(), "Modo Lectura activado", Toast.LENGTH_SHORT).show()
    }

    private fun desactivarModoLectura() {
        binding.btnAgregarReceta.visibility = View.VISIBLE
        binding.tvMensajeRecetas.visibility = View.VISIBLE // Muestra el título nuevamente
        binding.recycleRecetas.visibility = View.VISIBLE

        binding.btnModoLectura.visibility = View.VISIBLE
        binding.btnSalirModoLectura.visibility = View.GONE

        // Restaurar la UI al estado normal
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

        Toast.makeText(requireContext(), "Modo Lectura desactivado", Toast.LENGTH_SHORT).show()
    }

    private fun cargarMisRecetas() {
        val recetas = modelo.obtenerRecetasDatosPorUsuario(idUsuario)

        if (recetas.isNotEmpty()) {
            for (receta in recetas) {
                receta.ingredientes = modelo.optenerLosIngredientesPorIdRecetas(receta.idReceta)
            }
            val unidadSeleccionada = recetas.firstOrNull()?.ingredientes?.firstOrNull()?.unidad ?: "g"
            binding.tvMensajeRecetas.visibility = View.GONE
            binding.recycleRecetas.visibility = View.VISIBLE
            binding.recycleRecetas.adapter = RecetaAdapter(recetas, this@RecetaFragment, unidadSeleccionada)
        } else {
            binding.tvMensajeRecetas.visibility = View.VISIBLE
            binding.recycleRecetas.visibility = View.GONE
        }
    }

    private fun configuracionRecycle() {
        binding.recycleRecetas.layoutManager = LinearLayoutManager(context)
        binding.recycleRecetas.setHasFixedSize(true)
    }

    override fun clicEditarReceta(receta: RecetaDatos, position: Int) {
        val intent = Intent(requireContext(), EditarRecetaActivity::class.java)
        intent.putExtra("idReceta", receta.idReceta)
        startActivity(intent)
    }

    override fun clicEliminarReceta(receta: RecetaDatos, position: Int) {
        val eliminado = modelo.eliminarReceta(receta.idReceta)
        if (eliminado > 0) {
            Toast.makeText(context, "Receta eliminada", Toast.LENGTH_SHORT).show()
            cargarMisRecetas()
        } else {
            Toast.makeText(context, "Error al eliminar la receta", Toast.LENGTH_SHORT).show()
        }
    }

    override fun clicCalificarReceta(receta: RecetaDatos, position: Int) {
        val intent = Intent(requireContext(), CalificarRecetaActivity::class.java)
        intent.putExtra("idReceta", receta.idReceta)
        intent.putExtra("nombreReceta", receta.nombreReceta)
        startActivity(intent)
    }

    override fun clicCompartirReceta(receta: RecetaDatos, position: Int) {
        val mensaje = """
            Receta: ${receta.nombreReceta}
            Elaborada por: ${receta.nombreUsuario}
            Ingredientes: ${receta.ingredientes?.joinToString("\n") { "${it.cantidad} - ${it.nombre}" }}
            Tiempo: ${receta.tiempo}
            Elaboración: ${receta.preparacion}
            Presupuesto: ${receta.presupuesto}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, mensaje)
        startActivity(Intent.createChooser(intent, "Compartir Receta!"))
    }

    companion object {
        @JvmStatic
        fun newInstance(idUsuario: Int): RecetaFragment {
            val fragment = RecetaFragment()
            val args = Bundle().apply {
                putInt(ARG_ID_USUARIO, idUsuario)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
