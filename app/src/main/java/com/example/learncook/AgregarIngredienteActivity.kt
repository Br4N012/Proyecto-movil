package com.example.learncook

import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.learncook.databinding.ActivityAgregarIngredienteBinding
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Ingrediente
import com.example.learncook.utilidades.ToastHelper

class AgregarIngredienteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgregarIngredienteBinding
    private lateinit var db: LearnCookDB
    private lateinit var unidadAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarIngredienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = LearnCookDB(this)

        // Opciones del spinner con una opción por defecto
        val unidades = listOf("Seleccionar unidad", "kg", "l", "pz")
        unidadAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, unidades)
        unidadAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spUnidadAgregar.adapter = unidadAdapter

        // Acción al hacer clic en el botón de agregar
        binding.btnGuardarIngrediente.setOnClickListener {
            ToastHelper.vibrate(this)
            val nombre = binding.etNombre.text.toString().trim()
            val precio = binding.etPrecio.text.toString().toDoubleOrNull()
            val unidad = binding.spUnidadAgregar.selectedItem.toString()

            if (nombre.isNotEmpty() && precio != null && unidad != "Seleccionar unidad") {
                val ingrediente = Ingrediente(
                    id = 0,
                    nombre = nombre,
                    precio = precio,
                    cantidad = 0.0,
                    unidad = unidad
                )
                val resultado = db.agregarIngrediente(ingrediente)
                if (resultado > 0) {
                    ToastHelper.showSuccess(this,"Ingrediente agregado correctamente")
                    limpiarCampos()
                } else {
                    ToastHelper.showError(this,"Error al agregar el ingrediente")
                }
            } else {
                ToastHelper.showWarning(this,"Completa todos los campos correctamente")
            }
        }
    }

    private fun limpiarCampos() {
        binding.etNombre.text.clear()
        binding.etPrecio.text.clear()
        binding.spUnidadAgregar.setSelection(0)
    }

    private fun showToast(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}
