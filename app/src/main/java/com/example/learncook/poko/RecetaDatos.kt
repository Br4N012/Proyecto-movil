package com.example.learncook.poko


data class RecetaDatos(val idReceta: Int, val nombreUsuario: String, val nombreReceta: String, val tiempo: String, val presupuesto: Double, val preparacion: String, var ingredientes: MutableList<Ingrediente>?,  val imagenUri: String? = null // Nuevo campo
 )