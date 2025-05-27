package com.example.learncook.utilidades

import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.learncook.databinding.ActivityIngredientesBinding
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Ingrediente

class IngredientesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIngredientesBinding
    private lateinit var db: LearnCookDB
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var unidadAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIngredientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = LearnCookDB(this)

        // Lista de unidades para ambos spinners
        val unidades = listOf("kg", "l", "pz")
        unidadAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, unidades)
        unidadAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        binding.spUnidadAgregar.adapter = unidadAdapter
        binding.spUnidadActualizar.adapter = unidadAdapter

        mostrarIngredientes()

        // Agregar ingrediente
        binding.btnAgregar.setOnClickListener {
            val nombre = binding.etNombre.text.toString()
            val precio = binding.etPrecio.text.toString().toDoubleOrNull()
            val unidadSeleccionada = binding.spUnidadAgregar.selectedItem.toString()

            if (nombre.isNotEmpty() && precio != null) {
                val ingrediente = Ingrediente(
                    id = 0,
                    nombre = nombre,
                    precio = precio,
                    cantidad = 0.0,
                    unidad = unidadSeleccionada
                )
                val resultado = db.agregarIngrediente(ingrediente)
                if (resultado > 0) {
                    showToast("Ingrediente agregado")
                    limpiarCamposAgregar()
                    mostrarIngredientes()
                } else {
                    showToast("Error al agregar")
                }
            } else {
                showToast("Completa nombre y precio correctamente")
            }
        }

        // Eliminar ingrediente
        binding.btnEliminar.setOnClickListener {
            val id = binding.etIdEliminar.text.toString().toIntOrNull()
            if (id != null) {
                val exito = db.borrarIngrediente(id)
                showToast(if (exito) "Ingrediente eliminado" else "No se encontró el ID")
                limpiarCamposEliminar()
                mostrarIngredientes()
            } else {
                showToast("Ingresa un ID válido")
            }
        }

        // Actualizar ingrediente (incluyendo unidad)
        // Actualizar ingrediente
        binding.btnActualizar.setOnClickListener {
            val id = binding.etIdActualizar.text.toString().toIntOrNull()
            val nuevoNombre = binding.etNuevoNombre.text.toString()
            val nuevoPrecio = binding.etNuevoPrecio.text.toString().toDoubleOrNull()
            val nuevaUnidad = binding.spUnidadActualizar.selectedItem.toString()

            if (id != null && nuevoNombre.isNotEmpty() && nuevoPrecio != null) {
                val exito = db.actualizarIngrediente(id, nuevoNombre, nuevoPrecio, nuevaUnidad)
                showToast(if (exito) "Ingrediente actualizado" else "No se encontró el ID")
                limpiarCamposActualizar()
                mostrarIngredientes()
            } else {
                showToast("Completa todos los campos correctamente")
            }
        }

    }

    private fun mostrarIngredientes() {
        val ingredientes = db.obtenerIngredientes()
        val listaStrings = ingredientes.map {
            "ID: ${it.id} - ${it.nombre} ($${it.precio}) - ${it.unidad ?: "sin unidad"}"
        }
        adapter = ArrayAdapter(this, R.layout.simple_list_item_1, listaStrings)
        binding.listaIngredientes.adapter = adapter
    }

    private fun limpiarCamposAgregar() {
        binding.etNombre.text.clear()
        binding.etPrecio.text.clear()
        binding.spUnidadAgregar.setSelection(0)
    }
    private fun limpiarCamposActualizar() {
        binding.etIdActualizar.text.clear()
        binding.etNuevoNombre.text.clear()
        binding.etNuevoPrecio.text.clear()
        binding.spUnidadActualizar.setSelection(0)
    }
    private fun limpiarCamposEliminar() {
        binding.etIdEliminar.text.clear()
    }


    private fun showToast(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}
