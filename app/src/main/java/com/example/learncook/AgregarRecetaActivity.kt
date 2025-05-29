package com.example.learncook

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.learncook.databinding.ActivityAgregarRecetaBinding
import com.example.learncook.modelo.LearnCookDB
import com.example.learncook.poko.Ingrediente
import com.example.learncook.poko.Receta
import com.example.learncook.utilidades.ToastHelper
import com.example.learncook.utilidades.IngredientesActivity

class AgregarRecetaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgregarRecetaBinding
    private lateinit var modelo: LearnCookDB
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var unidadesAdapter: ArrayAdapter<String>
    private lateinit var listaDeIngredientes: MutableList<Ingrediente>

    private var idUsuario = 0
    private var presupuesto = 0.0
    private val listaId = mutableListOf<Int>()
    private val listaCantidad = mutableListOf<Double>()
    private val listaUnidades = mutableListOf<String>()
    private val listaUnidaddes = listOf("kg", "g", "l", "ml", "tz", "cda", "pz")
    private var unidadSeleccionada = "g"
    private var imagenUri: Uri? = null
    private lateinit var unidadesConOpcionInicial: List<String>

    private val seleccionarImagenLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado: ActivityResult ->
        if (resultado.resultCode == Activity.RESULT_OK) {
            resultado.data?.data?.let { uri ->
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                imagenUri = uri
                binding.ivImagenReceta.setImageURI(uri)
            }
        }
    }

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
        binding = ActivityAgregarRecetaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modelo = LearnCookDB(this)
        idUsuario = intent.getIntExtra("idUsuario", -1)
        listaDeIngredientes = modelo.traerIngredientes().toMutableList()

        configurarSpinners()
        configurarListeners()
        aplicarPreferenciasVisuales()
    }

    private fun configurarSpinners() {
        val nombresIngredientes = mutableListOf("Seleccionar ingrediente")
        nombresIngredientes.addAll(
            listaDeIngredientes.map {
                val unidadMostrada = it.unidad?.takeIf { u -> u.isNotBlank() } ?: "sin unidad"
                "${it.nombre} ($${it.precio}) - $unidadMostrada"
            }
        )

        adapter = ArrayAdapter(this, R.layout.spiner_item, nombresIngredientes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spIngredientesReceta.adapter = adapter

        unidadesConOpcionInicial = listOf("Seleccionar unidad de medida") + listaUnidaddes
        unidadesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, unidadesConOpcionInicial)
        unidadesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spUnidadMedida.adapter = unidadesAdapter

        val tiempoAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.unidades_tiempo,
            android.R.layout.simple_spinner_item
        )
        tiempoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUnidadTiempo.adapter = tiempoAdapter
    }

    private fun configurarListeners() {
        binding.spUnidadMedida.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 0) return
                unidadSeleccionada = unidadesConOpcionInicial[position]

                val posIngrediente = binding.spIngredientesReceta.selectedItemPosition
                if (posIngrediente >= 1 && posIngrediente < listaDeIngredientes.size + 1) {
                    val ingredienteSeleccionado = listaDeIngredientes[posIngrediente - 1]
                    mostrarDialogCantidad(ingredienteSeleccionado)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.spIngredientesReceta.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // No se hace nada al seleccionar ingrediente
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.spIngredientesReceta.setSelection(0)
            }
        }

        binding.btnAgregarReceta.setOnClickListener {
            ToastHelper.vibrate(this@AgregarRecetaActivity)
            if (validarDatos()) {
                guardarReceta()
            }
        }
        binding.btnAgregarNuevoIngrediente.setOnClickListener {
            val intent = Intent(this, AgregarIngredienteActivity::class.java)
            intent.putExtra("idUsuario", idUsuario)  // Si lo necesitas
            startActivity(intent)
        }

        binding.ivImagenReceta.setOnClickListener {
            verificarYPedirPermisos()
        }
    }

    private fun verificarYPedirPermisos() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
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
            .setMessage("Esta aplicación necesita acceso a tus imágenes para poder agregar fotos a las recetas")
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

    private fun guardarReceta() {
        val tiempoTexto = "${binding.etNumeroTiempo.text} ${binding.spinnerUnidadTiempo.selectedItem}"

        val receta = Receta(
            0,
            idUsuario,
            binding.etNombreReceta.text.toString(),
            tiempoTexto,
            presupuesto,
            binding.etPreparacion.text.toString(),
            imagenUri?.toString() ?: ""
        )

        val agregado = modelo.agregarReceta(receta)
        if (agregado > 0) {
            val ultimoId = modelo.traerUltimoIdDeReceta()
            val agregadoIngredientes = modelo.agregarIngredientes(
                ultimoId,
                listaId,
                listaCantidad,
                listaUnidades
            )

            if (agregadoIngredientes == 1) {
                ToastHelper.showSuccess(this, "Receta creada")
                finish()
            } else {
                ToastHelper.showError(this, "Error al agregar los ingredientes")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        listaDeIngredientes.clear()
        listaDeIngredientes.addAll(modelo.obtenerIngredientes())
        configurarSpinners()
        aplicarPreferenciasVisuales()
    }

    private fun aplicarPreferenciasVisuales() {
        val prefs = getSharedPreferences("config_visual", MODE_PRIVATE)
        val tamTexto = prefs.getFloat("tam_texto", 20f)
        val tamBoton = prefs.getFloat("tam_boton", 20f)
        val modoOscuro = prefs.getBoolean("modo_oscuro", false)

        val colorFondo = if (modoOscuro) android.graphics.Color.BLACK else android.graphics.Color.parseColor("#FCFFDF")
        val colorTexto = if (modoOscuro) android.graphics.Color.WHITE else android.graphics.Color.parseColor("#021D3D")
        val colorBoton = if (modoOscuro) android.graphics.Color.DKGRAY else android.graphics.Color.parseColor("#4CAF50")

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

    private fun mostrarDialogCantidad(ingrediente: Ingrediente) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cantidad de ${ingrediente.nombre}")

        val input = EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Ej: 250"
        }

        builder.setView(input)
        builder.setPositiveButton("OK") { _, _ ->
            input.text.toString().toDoubleOrNull()?.let { cantidad ->
                if (cantidad > 0) {
                    agregarIngredienteALista(ingrediente, cantidad)
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

    private fun agregarIngredienteALista(ingrediente: Ingrediente, cantidad: Double) {
        listaCantidad.add(cantidad)
        listaUnidades.add(unidadSeleccionada)
        listaId.add(ingrediente.id)

        val textoActual = binding.etIngredientes.text.toString()
        binding.etIngredientes.setText(
            "$textoActual${ingrediente.nombre} - $cantidad $unidadSeleccionada\n"
        )
        presupuesto += ingrediente.precio * cantidad
        binding.spIngredientesReceta.setSelection(0)
        binding.spUnidadMedida.setSelection(0)
    }

    private fun validarDatos(): Boolean {
        return when {
            binding.etNombreReceta.text.isEmpty() -> {
                ToastHelper.showWarning(this, "Ingresa un nombre para la receta")
                false
            }
            binding.etNumeroTiempo.text.isEmpty() -> {
                ToastHelper.showWarning(this, "Ingresa el tiempo de preparación")
                false
            }
            binding.etPreparacion.text.isEmpty() -> {
                ToastHelper.showWarning(this, "Ingresa los pasos de preparación")
                false
            }
            binding.etIngredientes.text.isEmpty() -> {
                ToastHelper.showWarning(this, "Agrega al menos un ingrediente")
                false
            }
            else -> true
        }
    }
}
