package com.example.learncook.fragmentos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.learncook.BuscarIngredienteActivity
import com.example.learncook.BuscarPresupuestoActivity
import com.example.learncook.databinding.FragmentHomeBinding
import com.example.learncook.utilidades.IngredientesActivity

private const val ARG_ID_USUARIO = "idUsuario"

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var idUsuario: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idUsuario = it.getInt(ARG_ID_USUARIO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aplicarPreferenciasVisuales()

        // Configurar los listeners para los botones
        binding.btnBuscarP.setOnClickListener {
            val intent = Intent(requireContext(), BuscarPresupuestoActivity::class.java)
            intent.putExtra(ARG_ID_USUARIO, idUsuario)
            startActivity(intent)
        }

        binding.btnBuscarI.setOnClickListener {
            val intent = Intent(requireContext(), BuscarIngredienteActivity::class.java)
            intent.putExtra(ARG_ID_USUARIO, idUsuario)
            startActivity(intent)
        }

        binding.btnGestionarIngredientes.setOnClickListener {
            val intent = Intent(requireContext(), IngredientesActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        aplicarPreferenciasVisuales()
    }

    private fun aplicarPreferenciasVisuales() {
        val prefs = requireContext().getSharedPreferences("config_visual", Context.MODE_PRIVATE)
        val tamTexto = prefs.getFloat("tam_texto", 16f)
        val tamBoton = prefs.getFloat("tam_boton", 48f)
        val modoOscuro = prefs.getBoolean("modo_oscuro", false)

        val colorFondo = if (modoOscuro) android.graphics.Color.BLACK else android.graphics.Color.parseColor("#FCFFDF")
        val colorTexto = if (modoOscuro) android.graphics.Color.WHITE else android.graphics.Color.parseColor("#021D3D")
        val colorBoton = if (modoOscuro) android.graphics.Color.GRAY else android.graphics.Color.parseColor("#4CAF50")

        // Cambiar fondo del fragmento
        binding.root.setBackgroundColor(colorFondo)

        val textos = listOf(
            binding.btnBuscarP,
            binding.btnBuscarI,
            binding.btnGestionarIngredientes
        )

        // Aplicar colores y tamaños de texto
        for (txt in textos) {
            txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, tamTexto)
            txt.setTextColor(colorTexto)
        }

        // Aplicar fondo gris en modo oscuro y verde en modo normal
        val botones = textos.filterIsInstance<Button>()
        for (btn in botones) {
            btn.setBackgroundColor(colorBoton)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(idUsuario: Int): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle().apply {
                putInt(ARG_ID_USUARIO, idUsuario)
            }
            fragment.arguments = args
            return fragment
        }
    }
}