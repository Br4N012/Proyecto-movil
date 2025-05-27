package com.example.learncook

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.learncook.adaptadores.IngredienteAdapter
import com.example.learncook.databinding.ActivityEditarRecetaBinding
import com.example.learncook.interfaces.ListenerRecycleIngrediente
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Ingrediente
import com.example.learncook.poko.Receta
import com.example.learncook.utilidades.ToastHelper

class EditarRecetaActivity : AppCompatActivity(), ListenerRecycleIngrediente {
    private lateinit var binding: ActivityEditarRecetaBinding
    private lateinit var modelo: LearnCookDB
    private lateinit var ingredientesAdapter: IngredienteAdapter
    private lateinit var listaDeIngredientes: MutableList<Ingrediente>
    private lateinit var ingredientes: List<Ingrediente>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var unidadesAdapter: ArrayAdapter<String>
    private var idReceta = -1
    private var presupuesto = 0.0
    private val listaUnidades = listOf("kg", "g", "l", "ml", "tz", "cda", "pz")
    private var unidadSeleccionada = "g"
    private var imagenUri: Uri? = null

    // Launcher para selección de imágenes
    private val seleccionarImagenLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == Activity.RESULT_OK) {
            resultado.data?.data?.let { uri ->
                // Tomar permisos persistentes para la URI
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                imagenUri = uri
                binding.ivImagenReceta.setImageURI(uri)
            }
        }
    }

    // Launcher para solicitud de permisos
    private val solicitarPermisoImagenes = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            abrirSelectorImagenes()
        } else {
            ToastHelper.showWarning(this, "Se necesita permiso para acceder a las imágenes")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarRecetaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        modelo = LearnCookDB(this@EditarRecetaActivity)
        idReceta = intent.getIntExtra("idReceta", -1)

        aplicarPreferenciasVisuales()

        listaDeIngredientes = modelo.traerIngredientes().toMutableList()

        configurarSpinners()
        configuracionRecycle()

        // Configurar listeners para la imagen
        binding.ivImagenReceta.setOnClickListener {
            verificarYPedirPermisos()
        }

        binding.ivImagenReceta.setOnClickListener {
            verificarYPedirPermisos()
        }

        val receta = modelo.obtenerReceta(idReceta)
        if (receta != null) {
            llenarDatos(receta)
            ingredientes = modelo.optenerLosIngredientesPorIdRecetas(idReceta)
            actualizarPresupuesto()
            traerIngredientes()
        } else {
            Toast.makeText(this, "Error al obtener la receta", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnEditarReceta.setOnClickListener {
            if (validarDatos()) {
                val recetaActualizada = Receta(
                    idReceta,
                    receta?.idUsuario ?: 0,
                    binding.etNombreReceta.text.toString(),
                    obtenerTiempoConUnidad(),
                    presupuesto,
                    binding.etPreparacion.text.toString(),
                    imagenUri?.toString() ?: ""
                )

                if (modelo.modificarReceta(recetaActualizada) > 0) {
                    ToastHelper.showSuccess(this, "Receta actualizada")
                    finish()
                } else {
                    ToastHelper.showError(this, "Error al actualizar")
                }
            }
        }
    }

    private fun configurarSpinners() {
        // Spinner de ingredientes
        adapter = ArrayAdapter(
            this,
            R.layout.spiner_item,
            listaDeIngredientes.map { it.nombre }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spIngredientesEditados.adapter = this
        }

        // Spinner de unidades de medida
        unidadesAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listaUnidades
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spUnidadMedida.adapter = this
        }

        // Spinner de unidades de tiempo
        ArrayAdapter.createFromResource(
            this,
            R.array.unidades_tiempo,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spUnidadTiempo.adapter = adapter
        }

        // Listeners
        binding.spUnidadMedida.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                unidadSeleccionada = listaUnidades[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                unidadSeleccionada = "g"
            }
        }

        binding.spIngredientesEditados.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= 1) {
                    mostrarDialogCantidad(listaDeIngredientes[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.spIngredientesEditados.setSelection(0)
            }
        }
    }

    private fun verificarYPedirPermisos() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                abrirSelectorImagenes()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                mostrarExplicacionPermisos()
            }
            else -> {
                solicitarPermisoImagenes.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun mostrarExplicacionPermisos() {
        AlertDialog.Builder(this)
            .setTitle("Permiso necesario")
            .setMessage("Esta aplicación necesita acceso a tus imágenes para poder cambiar la foto de la receta")
            .setPositiveButton("Entendido") { _, _ ->
                solicitarPermisoImagenes.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun abrirSelectorImagenes() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        seleccionarImagenLauncher.launch(intent)
    }

    private fun obtenerTiempoConUnidad(): String {
        return "${binding.etTiempoReceta.text} ${binding.spUnidadTiempo.selectedItem}"
    }

    override fun onResume() {
        super.onResume()
        aplicarPreferenciasVisuales()
    }

    private fun aplicarPreferenciasVisuales() {
        val prefs = getSharedPreferences("config_visual", Context.MODE_PRIVATE)
        val tamTexto = prefs.getFloat("tam_texto", 20f)
        val modoOscuro = prefs.getBoolean("modo_oscuro", false)

        val colorFondo = if (modoOscuro) android.graphics.Color.BLACK else android.graphics.Color.parseColor("#FCFFDF")
        val colorTexto = if (modoOscuro) android.graphics.Color.parseColor("#ADD8E6") else android.graphics.Color.parseColor("#021D3D")

        binding.root.setBackgroundColor(colorFondo)
        binding.etNombreReceta.setTextColor(colorTexto)
        binding.etTiempoReceta.setTextColor(colorTexto)
        binding.etPreparacion.setTextColor(colorTexto)
        binding.btnEditarReceta.setTextColor(colorTexto)
        binding.spIngredientesEditados.setBackgroundColor(colorFondo)

        binding.etNombreReceta.textSize = tamTexto
        binding.etTiempoReceta.textSize = tamTexto
        binding.etPreparacion.textSize = tamTexto
        binding.btnEditarReceta.textSize = tamTexto
    }

    private fun actualizarPresupuesto() {
        presupuesto = 0.0
        for (ingre in ingredientes) {
            val cantidad = ingre.cantidad
            val precio = ingre.precio
            presupuesto += cantidad * precio
        }
    }

    private fun llenarDatos(receta: Receta) {
        binding.etNombreReceta.setText(receta.nombreReceta)

        // Extraer tiempo y unidad
        val tiempoNumero = receta.tiempo.split(" ").firstOrNull() ?: ""
        binding.etTiempoReceta.setText(tiempoNumero)

        val tiempoUnidad = receta.tiempo.split(" ").lastOrNull() ?: "minutos"
        val unidades = resources.getStringArray(R.array.unidades_tiempo)
        val posicionUnidad = unidades.indexOf(tiempoUnidad.lowercase())
        if (posicionUnidad >= 0) {
            binding.spUnidadTiempo.setSelection(posicionUnidad)
        }

        binding.etPreparacion.setText(receta.preparacion)

        // Cargar imagen si existe
        if (receta.imagenUri?.isEmpty() == true) {
            imagenUri = Uri.parse(receta.imagenUri)
            binding.ivImagenReceta.setImageURI(imagenUri)
        }
    }

    private fun validarDatos(): Boolean {
        if (binding.etNombreReceta.text.isNullOrEmpty()) {
            ToastHelper.showWarning(this, "Ingresa un nombre para la receta")
            return false
        }
        if (binding.etTiempoReceta.text.isNullOrEmpty()) {
            ToastHelper.showWarning(this, "Ingresa el tiempo de preparación")
            return false
        }
        if (binding.etPreparacion.text.isNullOrEmpty()) {
            ToastHelper.showWarning(this, "Ingresa los pasos de preparación")
            return false
        }
        if (ingredientes.isEmpty()) {
            ToastHelper.showWarning(this, "Agrega al menos un ingrediente")
            return false
        }
        return true
    }

    override fun clicEliminarIngrediente(ingrediente: Ingrediente, position: Int) {
        if (modelo.eliminarIngrediente(ingrediente.id) > 0) {
            actualizarPantalla()
            ToastHelper.showSuccess(this, "Ingrediente eliminado")
        } else {
            ToastHelper.showError(this, "Error al eliminar")
        }
    }

    override fun clicEditarIngrediente(ingrediente: Ingrediente, position: Int, etCantidad: EditText) {
        val cantidad = etCantidad.text.toString().toDoubleOrNull() ?: 0.0
        if (cantidad > 0) {
            ingrediente.cantidad = cantidad
            if (modelo.editarIngrediente(ingrediente) > 0) {
                actualizarPantalla()
                ToastHelper.showSuccess(this, "Ingrediente actualizado")
            }
        } else {
            ToastHelper.showWarning(this, "Cantidad inválida")
        }
    }

    private fun traerIngredientes() {
        if (ingredientes.isNotEmpty()) {
            binding.recycleIngredientes.visibility = View.VISIBLE
            binding.recycleIngredientes.adapter = IngredienteAdapter(ingredientes, this)
        } else {
            Toast.makeText(this, "No hay ingredientes", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configuracionRecycle() {
        binding.recycleIngredientes.layoutManager = LinearLayoutManager(this)
        binding.recycleIngredientes.setHasFixedSize(true)
    }

    private fun mostrarDialogCantidad(ingrediente: Ingrediente) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cantidad de ${ingrediente.nombre}")

        val input = EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Ej: 250"
        }

        builder.setView(input)
        builder.setPositiveButton("OK") { _, _ ->
            input.text.toString().toDoubleOrNull()?.let { cantidad ->
                if (cantidad > 0) {
                    ingrediente.cantidad = cantidad
                    val agregado = modelo.agregarIngrediente(idReceta, ingrediente)
                    if (agregado > 0) {
                        actualizarPresupuesto()
                        ToastHelper.showSuccess(this, "Ingrediente agregado")
                        actualizarPantalla()
                    } else {
                        ToastHelper.showError(this, "Error al agregar ingrediente")
                    }
                    binding.spIngredientesEditados.setSelection(0)
                } else {
                    ToastHelper.showWarning(this, "La cantidad debe ser mayor a cero")
                }
            } ?: run {
                ToastHelper.showWarning(this, "Cantidad inválida")
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun actualizarPantalla() {
        ingredientes = modelo.optenerLosIngredientesPorIdRecetas(idReceta)
        traerIngredientes()
    }
}