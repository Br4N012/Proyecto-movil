package com.example.learncook.poko

data class Receta(val id:Int, val idUsuario: Int, val nombreReceta:String, val tiempo: String, val presupuseto: Double, val preparacion: String,  val imagenUri: String? = null // Nuevo campo (nullable)
)
