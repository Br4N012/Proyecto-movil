package com.example.learncook.interfaces

import com.example.learncook.poko.Receta
import com.example.learncook.poko.RecetaDatos

interface ListenerRecycleReceta {
    fun clicEditarReceta(receta:RecetaDatos, position: Int)
    fun clicEliminarReceta(receta: RecetaDatos, position: Int)
    fun clicCalificarReceta(receta: RecetaDatos, position: Int)
    fun clicCompartirReceta(receta: RecetaDatos, position: Int)
}