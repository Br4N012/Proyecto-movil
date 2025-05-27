package com.example.learncook.fragmentos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.learncook.EditarPerfilActivity
import com.example.learncook.LoginActivity
import com.example.learncook.SeguirUsuarioActivity
import com.example.learncook.databinding.FragmentPerfilBinding
import com.example.learncook.modelo.LearnCookDB

private const val ARG_ID_USUARIO = "idUsuario"

class PerfilFragment : Fragment() {
    private lateinit var binding: FragmentPerfilBinding
    private lateinit var modelo: LearnCookDB
    private var idUsuario: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idUsuario = it.getInt(ARG_ID_USUARIO)
        }
        modelo = LearnCookDB(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aplicarPreferenciasVisuales()
        configurarEventos()
    }

    override fun onResume() {
        super.onResume()
        aplicarPreferenciasVisuales()
    }

    private fun aplicarPreferenciasVisuales() {
        val prefs = requireContext().getSharedPreferences("config_visual", Context.MODE_PRIVATE)
        val tamTexto = prefs.getFloat("tam_texto", 20f)
        val tamBoton = prefs.getFloat("tam_boton", 45f)
        val modoOscuro = prefs.getBoolean("modo_oscuro", false)

        val colorFondo = if (modoOscuro) android.graphics.Color.BLACK else android.graphics.Color.parseColor("#FCFFDF")
        val colorTexto = if (modoOscuro) android.graphics.Color.WHITE else android.graphics.Color.parseColor("#021D3D")
        val colorBoton = if (modoOscuro) android.graphics.Color.GRAY else android.graphics.Color.parseColor("#4CAF50") // Verde

        // Cambiar fondo del fragmento
        binding.root.setBackgroundColor(colorFondo)

        val textos = listOf(
            binding.nombreUser,
            binding.btnEditarP,
            binding.btnEliminarP,
            binding.btnBuscar,
            binding.btnConfigFuente,
            binding.btnConfigBoton,
            binding.btnCerrarSesion,
            binding.btnModoOscuro
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

    private fun configurarEventos() {
        binding.btnEditarP.setOnClickListener {
            val intent = Intent(requireContext(), EditarPerfilActivity::class.java)
            intent.putExtra("idUsuario", idUsuario)
            startActivity(intent)
        }

        binding.btnEliminarP.setOnClickListener {
            mostrarDialogoConfirmacion()
        }

        binding.btnBuscar.setOnClickListener {
            val intent = Intent(requireContext(), SeguirUsuarioActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        binding.btnCerrarSesion.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        binding.btnConfigFuente.setOnClickListener {
            mostrarDialogoTamanioTexto()
        }

        binding.btnConfigBoton.setOnClickListener {
            mostrarDialogoTamanioBoton()
        }

        binding.btnModoOscuro.setOnClickListener {
            mostrarDialogoModoOscuro()
        }
    }

    private fun mostrarDialogoConfirmacion() {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar perfil")
            .setMessage("¿Estás seguro de que deseas eliminar tu perfil? Se perderá toda tu información.")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarCuenta()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarCuenta() {
        val eliminadoExitosamente = modelo.eliminarUsuario(idUsuario)
        if (eliminadoExitosamente) {
            mostrarMensaje("Cuenta eliminada correctamente.")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        } else {
            mostrarMensaje("Error al eliminar la cuenta. Por favor, inténtalo nuevamente.")
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun mostrarDialogoTamanioTexto() {
        val tamanios = arrayOf("Pequeño", "Mediano", "Grande")
        val valores = arrayOf(16f, 20f, 24f)

        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona tamaño de texto")
            .setItems(tamanios) { _, which ->
                val prefs = requireContext().getSharedPreferences("config_visual", Context.MODE_PRIVATE)
                prefs.edit().putFloat("tam_texto", valores[which]).apply()
                aplicarPreferenciasVisuales()
            }
            .show()
    }

    private fun mostrarDialogoTamanioBoton() {
        val tamanios = arrayOf("Pequeño", "Mediano", "Grande")
        val valores = arrayOf(35f, 45f, 55f)

        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona tamaño de botón")
            .setItems(tamanios) { _, which ->
                val prefs = requireContext().getSharedPreferences("config_visual", Context.MODE_PRIVATE)
                prefs.edit().putFloat("tam_boton", valores[which]).apply()
                aplicarPreferenciasVisuales()
            }
            .show()
    }

    private fun mostrarDialogoModoOscuro() {
        AlertDialog.Builder(requireContext())
            .setTitle("Modo oscuro")
            .setMessage("¿Deseas activar el modo oscuro?")
            .setPositiveButton("Sí") { _, _ ->
                cambiarModoOscuro(true)
            }
            .setNegativeButton("No") { _, _ ->
                cambiarModoOscuro(false)
            }
            .show()
    }

    private fun cambiarModoOscuro(activar: Boolean) {
        val prefs = requireContext().getSharedPreferences("config_visual", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("modo_oscuro", activar).apply()
        aplicarPreferenciasVisuales()
    }

    companion object {
        @JvmStatic
        fun newInstance(idUsuario: Int): PerfilFragment {
            val fragment = PerfilFragment()
            val args = Bundle().apply {
                putInt(ARG_ID_USUARIO, idUsuario)
            }
            fragment.arguments = args
            return fragment
        }
    }
}