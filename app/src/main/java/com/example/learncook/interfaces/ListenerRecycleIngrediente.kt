package com.example.learncook.interfaces

import android.widget.EditText
import com.example.learncook.poko.Ingrediente

interface ListenerRecycleIngrediente {
    fun clicEliminarIngrediente(ingrediente: Ingrediente, position: Int)
    fun clicEditarIngrediente(ingrediente: Ingrediente, position: Int, etCantidad: EditText)
}