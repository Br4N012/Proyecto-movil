package com.example.learncook.modelo

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.learncook.poko.Calificacion
import com.example.learncook.poko.CalificacionDatos
import com.example.learncook.poko.Ingrediente
import com.example.learncook.poko.Receta
import com.example.learncook.poko.RecetaDatos
import com.example.learncook.poko.Usuario

class  LearnCookDB(contexto: Context): SQLiteOpenHelper(contexto,NOMBRE_DB,null,VERSION_DB){
    companion object {
        private const val  NOMBRE_DB = "learnCook.bd"
        //tabla usuario
        private const val NOMBRE_TABLA_USUARIO ="usuario"
        private const val COL_ID_USUARIO = "id"
        private const val COL_CORREO = "correo"
        private const val COL_CONTRASENA = "contrasena"
        private const val COL_NOMBRE_USUARIO = "nombreUsuario"
        //tabla Receta
        private const val NOMBRE_TABLA_RECETA ="receta"
        private const val COL_ID_RECETA = "id"
        private const val COL_USUARIO_ID = "usuarioId"
        private const val COL_NOMBRE_RECETA = "nombreReceta"
        private const val COL_TIEMPO = "tiempo"
        private const val COL_PRESUPUESTO = "presupuesto"
        private const val COL_PREPARACION = "preparacion"
        private const val COL_IMAGEN_URI = "imagenUri"
        //tabla ingrediente
        private const val NOMBRE_TABLA_INGREDIENTE ="ingrediente"
        private const val COL_ID_INGREDIENTE = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_PRECIO = "precio"
        //tabla RecetaIngredientes
        private const val NOMBRE_TABLA_RECETAINGREDIENTES ="recetaIngredientes"
        private const val COL_ID_RECETAINGREDIENTES = "id"
        private const val COL_RECETA_ID = "recetaId"
        private const val COL_INGREDIENTE_ID = "ingredienteId"
        private const val COL_CANTIDAD = "cantidad"
        private const val COL_UNIDAD = "unidad"
        //tabla seguidor
        private const val NOMBRE_TABLA_SEGUIDOR ="seguidor"
        private const val COL_ID_SEGUIDOR = "id"
        private const val COL_USUARIO_ID_SEGUIDOR = "usuarioId"
        private const val COL_SEGUIDO_ID = "seguidoId"
        //tabla calificaciones
        private const val NOMBRE_TABLA_CALIFICACIONES ="calificaciones"
        private const val COL_ID_CALIFICACIONES = "id"
        private const val COL_USUARIO_ID_CALIFICACION = "usuarioId"
        private const val COL_RECETA_ID_CALIFICACION = "recetaId"
        private const val COL_PUNTUACION = "puntuacion"
        private const val COL_COMENTARIO = "comentario"
        private const val  VERSION_DB = 5
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_USUARIO = ("CREATE TABLE $NOMBRE_TABLA_USUARIO (" +
                "$COL_ID_USUARIO INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_CORREO TEXT NOT NULL UNIQUE, " +
                "$COL_CONTRASENA TEXT NOT NULL, " +
                "$COL_NOMBRE_USUARIO TEXT NOT NULL UNIQUE)")

        val CREATE_TABLE_RECETA = ("CREATE TABLE $NOMBRE_TABLA_RECETA (" +
                "$COL_ID_RECETA INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_USUARIO_ID INTEGER NOT NULL, " +
                "$COL_TIEMPO TEXT NOT NULL, " +
                "$COL_PRESUPUESTO REAL, " +
                "$COL_PREPARACION TEXT NOT NULL, " +
                "$COL_IMAGEN_URI TEXT," + // Nueva columna
                "FOREIGN KEY ($COL_USUARIO_ID) REFERENCES $NOMBRE_TABLA_USUARIO($COL_ID_USUARIO))")

        val CREATE_TABLE_INGREDIENTE = ("CREATE TABLE $NOMBRE_TABLA_INGREDIENTE (" +
                "$COL_ID_INGREDIENTE INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_NOMBRE TEXT NOT NULL UNIQUE, " +
                "$COL_PRECIO REAL NOT NULL, " +
                "$COL_UNIDAD TEXT)")

        val CREATE_TABLE_RECETAINGREDIENTES = ("CREATE TABLE $NOMBRE_TABLA_RECETAINGREDIENTES (" +
                "$COL_ID_RECETAINGREDIENTES INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_RECETA_ID INTEGER NOT NULL, " +
                "$COL_INGREDIENTE_ID INTEGER NOT NULL, " +
                "$COL_CANTIDAD REAL NOT NULL, " +
                "$COL_UNIDAD TEXT, " + // Campo nullable
                "FOREIGN KEY ($COL_RECETA_ID) REFERENCES $NOMBRE_TABLA_RECETA($COL_ID_RECETA), " +
                "FOREIGN KEY ($COL_INGREDIENTE_ID) REFERENCES $NOMBRE_TABLA_INGREDIENTE($COL_ID_INGREDIENTE), " +
                "UNIQUE ($COL_RECETA_ID, $COL_INGREDIENTE_ID))")

        val CREATE_TABLE_SEGUIDOR = ("CREATE TABLE $NOMBRE_TABLA_SEGUIDOR (" +
                "$COL_ID_SEGUIDOR INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_USUARIO_ID_SEGUIDOR INTEGER NOT NULL, " +
                "$COL_SEGUIDO_ID INTEGER NOT NULL, " +
                "FOREIGN KEY ($COL_USUARIO_ID_SEGUIDOR) REFERENCES $NOMBRE_TABLA_USUARIO($COL_ID_USUARIO), " +
                "FOREIGN KEY ($COL_SEGUIDO_ID) REFERENCES $NOMBRE_TABLA_USUARIO($COL_ID_USUARIO), " +
                "UNIQUE ($COL_USUARIO_ID_SEGUIDOR, $COL_SEGUIDO_ID))")

        val CREATE_TABLE_CALIFICACIONES = ("CREATE TABLE $NOMBRE_TABLA_CALIFICACIONES (" +
                "$COL_ID_CALIFICACIONES INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_USUARIO_ID_CALIFICACION INTEGER NOT NULL, " +
                "$COL_RECETA_ID_CALIFICACION INTEGER NOT NULL, " +
                "$COL_PUNTUACION INTEGER NOT NULL CHECK ($COL_PUNTUACION BETWEEN 1 AND 5), " +
                "$COL_COMENTARIO TEXT, " +
                "FOREIGN KEY ($COL_USUARIO_ID_CALIFICACION) REFERENCES $NOMBRE_TABLA_USUARIO($COL_ID_USUARIO), " +
                "FOREIGN KEY ($COL_RECETA_ID_CALIFICACION) REFERENCES $NOMBRE_TABLA_RECETA($COL_ID_RECETA), " +
                "UNIQUE ($COL_USUARIO_ID_CALIFICACION, $COL_RECETA_ID_CALIFICACION))")

        db!!.execSQL(CREATE_TABLE_USUARIO)
        db.execSQL(CREATE_TABLE_RECETA)
        db.execSQL(CREATE_TABLE_INGREDIENTE)
        db.execSQL(CREATE_TABLE_RECETAINGREDIENTES)
        db.execSQL(CREATE_TABLE_SEGUIDOR)
        db.execSQL(CREATE_TABLE_CALIFICACIONES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db?.execSQL("ALTER TABLE $NOMBRE_TABLA_RECETA ADD COLUMN $COL_NOMBRE_RECETA TEXT")

            val ingredientesEsenciales = listOf(
                Pair("Sal", 10.0),
                Pair("Pimienta", 7.0),
                Pair("Aceite de oliva", 13.0),
                Pair("Ajo", 15.5),
                Pair("Cebolla", 17.2),
                Pair("Tomate", 22.5),
                Pair("Papa", 30.6),
                Pair("Zanahoria", 25.0),
                Pair("Leche", 31.5),
                Pair("Huevos", 2.5),
                Pair("Harina", 25.2),
                Pair("Arroz", 19.9),
                Pair("Pasta", 11.0),
                Pair("Queso", 42.0),
                Pair("Pollo", 60.0),
                Pair("Carne de res", 80.0),
                Pair("Pescado", 21.0),
                Pair("Limón", 16.0),
                Pair("Cilantro", 11.2),
                Pair("Mantequilla", 27.5)
            )

            for (ingrediente in ingredientesEsenciales) {
                val contentValues = ContentValues().apply {
                    put(COL_NOMBRE, ingrediente.first)
                    put(COL_PRECIO, ingrediente.second)
                }
                db?.insert(NOMBRE_TABLA_INGREDIENTE, null, contentValues)
            }
        }
        if (oldVersion < 3) {
            db?.execSQL("ALTER TABLE $NOMBRE_TABLA_RECETAINGREDIENTES ADD COLUMN $COL_UNIDAD TEXT")
        }
        if(oldVersion < 4){
            db?.execSQL("ALTER TABLE $NOMBRE_TABLA_INGREDIENTE ADD COLUMN $COL_UNIDAD TEXT")
        }
        if(oldVersion <5){
            db?.execSQL("ALTER TABLE $NOMBRE_TABLA_RECETA ADD COLUMN $COL_IMAGEN_URI TEXT")
        }
    }

    fun agregarIngrediente(ingrediente: Ingrediente): Long {
        val db = this.writableDatabase
        val valores = ContentValues().apply {
            put(COL_NOMBRE, ingrediente.nombre)
            put(COL_PRECIO, ingrediente.precio)
            put(COL_UNIDAD, ingrediente.unidad)
            // Ya que no guardas cantidad en la BD, no la insertes aquí
        }
        return db.insert(NOMBRE_TABLA_INGREDIENTE, null, valores)
    }

    fun obtenerIngredientes(): List<Ingrediente> {
        val lista = mutableListOf<Ingrediente>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COL_ID_INGREDIENTE, $COL_NOMBRE, $COL_PRECIO, $COL_UNIDAD FROM $NOMBRE_TABLA_INGREDIENTE",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val ingrediente = Ingrediente(
                    id = cursor.getInt(0),
                    nombre = cursor.getString(1),
                    precio = cursor.getDouble(2),
                    cantidad = 0.0, // Sigue sin estar en la BD
                    unidad = cursor.getString(3)
                )
                lista.add(ingrediente)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }


    fun actualizarIngrediente(id: Int, nombreNuevo: String, precioNuevo: Double, unidadNueva: String): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put(COL_NOMBRE, nombreNuevo)
            put(COL_PRECIO, precioNuevo)
            put(COL_UNIDAD, unidadNueva)
        }
        return db.update(NOMBRE_TABLA_INGREDIENTE, valores, "$COL_ID_INGREDIENTE=?", arrayOf(id.toString())) > 0
    }
    fun borrarIngrediente(id: Int): Boolean {
        val db = writableDatabase
        return db.delete(NOMBRE_TABLA_INGREDIENTE, "$COL_ID_INGREDIENTE=?", arrayOf(id.toString())) > 0
    }

    fun agregarUsuario(usuario:Usuario): Long {
        val db =writableDatabase
        val valoresInsert = ContentValues()
        valoresInsert.put(COL_CORREO, usuario.correo)
        valoresInsert.put(COL_CONTRASENA,usuario.contrasena)
        valoresInsert.put(COL_NOMBRE_USUARIO, usuario.nombreUsuario)
        val filas = db.insert(NOMBRE_TABLA_USUARIO,null, valoresInsert)
        db.close()
        return filas
    }

    fun usuarioRegistrado(usuario: Usuario):Boolean{
        val db = readableDatabase
        val columnas = arrayOf(COL_ID_USUARIO)
        val filtro = "$COL_CORREO = ? AND $COL_CONTRASENA = ?"
        val clausua = arrayOf(usuario.correo, usuario.contrasena)
        val cursor = db.query(
            NOMBRE_TABLA_USUARIO,
            columnas,
            filtro,
            clausua,
            null,
            null,
            null
        )
        val registrado = cursor.count > 0
        cursor.close()
        db.close()
        return registrado
    }
    fun usuarioEnBase(correo: String):Boolean{
        val db = readableDatabase
        val columnas = arrayOf(COL_ID_USUARIO)
        val filtro = "$COL_CORREO = ? "
        val clausua = arrayOf(correo)
        val cursor = db.query(
            NOMBRE_TABLA_USUARIO,
            columnas,
            filtro,
            clausua,
            null,
            null,
            null
        )
        val registrado = cursor.count > 0
        cursor.close()
        db.close()
        return registrado
    }
    fun traerUsuario(user: Usuario): Usuario?{
        val db = readableDatabase
        val columnas = arrayOf(COL_ID_USUARIO, COL_CORREO, COL_CONTRASENA, COL_NOMBRE_USUARIO)
        val selection = "$COL_CORREO = ?"
        val selectionArgs = arrayOf(user.correo)
        val cursor = db.query(
            NOMBRE_TABLA_USUARIO,
            columnas,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_USUARIO))
            val correo = cursor.getString(cursor.getColumnIndexOrThrow(COL_CORREO))
            val contrasena = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTRASENA))
            val nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE_USUARIO))
            usuario = Usuario(id, correo, contrasena, nombreUsuario)
        }
        cursor.close()
        db.close()
        return usuario
    }

    fun isCorreo(correo: String): Boolean {
        val db = readableDatabase
        val columnas = arrayOf(COL_ID_USUARIO)
        val filtro = "$COL_CORREO = ? "
        val clausua = arrayOf(correo)
        val cursor = db.query(
            NOMBRE_TABLA_USUARIO,
            columnas,
            filtro,
            clausua,
            null,
            null,
            null
        )
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }




    fun actualizarContrasena(correo: String, contrasena: String):Int{
        val db = writableDatabase
        val valoresUpdate = ContentValues().apply {
            put(COL_CONTRASENA,contrasena)
        }
        val filasA = db.update(NOMBRE_TABLA_USUARIO,valoresUpdate,"$COL_CORREO = ? ", arrayOf(correo))
        db.close()
        return filasA
    }


    fun usuarioNombreRegistrado(nombreUsuario: String): Boolean {
        val db = readableDatabase
        val columnas = arrayOf(COL_ID_USUARIO)
        val filtro = "$COL_NOMBRE_USUARIO = ? "
        val clausua = arrayOf(nombreUsuario)
        val cursor = db.query(
            NOMBRE_TABLA_USUARIO,
            columnas,
            filtro,
            clausua,
            null,
            null,
            null
        )
        val registrado = cursor.count > 0
        cursor.close()
        db.close()
        return registrado
    }
    @SuppressLint("Range")
    fun traerIngredientes(): List<Ingrediente>{
        val misIngredientes = mutableListOf<Ingrediente>()
        misIngredientes.add(Ingrediente(-1,"Selecciona Un Ingrediente",0.0,0.0))
        val db = readableDatabase
        val resultadoConsulta: Cursor? = db.query(
            NOMBRE_TABLA_INGREDIENTE, null, null,
            null, null, null, null)
        if(resultadoConsulta!=null){
            while (resultadoConsulta.moveToNext()){
                val id = resultadoConsulta.getInt(resultadoConsulta.getColumnIndex(COL_ID_INGREDIENTE))
                val nombre = resultadoConsulta.getString(resultadoConsulta.getColumnIndex(COL_NOMBRE))
                val precio = resultadoConsulta.getDouble(resultadoConsulta.getColumnIndex(COL_PRECIO))
                val ingrediente = Ingrediente(id, nombre, precio, 0.0)
                misIngredientes.add(ingrediente)
            }
            resultadoConsulta.close()
        }
        db.close()
        return misIngredientes
    }

    fun agregarReceta(receta: Receta):Long {
        val db =writableDatabase
        val valoresInsert = ContentValues()
        valoresInsert.put(COL_USUARIO_ID, receta.idUsuario)
        valoresInsert.put(COL_NOMBRE_RECETA, receta.nombreReceta)
        valoresInsert.put(COL_TIEMPO,receta.tiempo)
        valoresInsert.put(COL_PRESUPUESTO, receta.presupuseto)
        valoresInsert.put(COL_PREPARACION, receta.preparacion)
        valoresInsert.put(COL_IMAGEN_URI, receta.imagenUri)
        val filas = db.insert(NOMBRE_TABLA_RECETA,null, valoresInsert)
        db.close()
        return filas
    }

    fun traerUltimoIdDeReceta(): Int {
        var ultimoId = -1
        val db = readableDatabase
        val query = "SELECT MAX($COL_ID_RECETA) FROM $NOMBRE_TABLA_RECETA"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            ultimoId = cursor.getInt(0)
        }

        cursor.close()
        db.close()

        return ultimoId
    }
    fun agregarIngredientes(
        id: Int,
        lista: List<Int>,
        listaCantidad: List<Double>,
        listaUnidades: MutableList<String>
    ): Int {
        val db = writableDatabase
        var resultado: Int = -1

        try {
            db.beginTransaction()
            for ((index, ingredienteId) in lista.withIndex()) {
                val contentValues = ContentValues().apply {
                    put(COL_RECETA_ID, id)
                    put(COL_INGREDIENTE_ID, ingredienteId)
                    put(COL_CANTIDAD, listaCantidad[index])
                    put(COL_UNIDAD, listaUnidades[index])
                }
                val insertResult = db.insert(NOMBRE_TABLA_RECETAINGREDIENTES, null, contentValues)
                if (insertResult == -1L) {
                    throw SQLException("Error al insertar ingredientes")
                }
            }
            db.setTransactionSuccessful()
            resultado = 1
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }
        return resultado
    }
    fun obtenerRecetasDatosPorUsuario(idUsuario: Int): List<RecetaDatos> {
        val recetasDatos = mutableListOf<RecetaDatos>()
        val db = readableDatabase

        val query = """
        SELECT 
            r.$COL_ID_RECETA, 
            u.$COL_NOMBRE_USUARIO, 
            r.$COL_NOMBRE_RECETA, 
            r.$COL_TIEMPO, 
            r.$COL_PRESUPUESTO, 
            r.$COL_PREPARACION,
            r.$COL_IMAGEN_URI
        FROM $NOMBRE_TABLA_RECETA r, $NOMBRE_TABLA_USUARIO u
        WHERE r.$COL_USUARIO_ID = ?  AND  u.$COL_ID_USUARIO = ?"""

        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(query, arrayOf(idUsuario.toString(),idUsuario.toString()))

            while (cursor.moveToNext()) {
                val idReceta = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_RECETA))
                val nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE_USUARIO))
                val nombreReceta = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE_RECETA))
                val tiempo = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIEMPO))
                val presupuesto = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRESUPUESTO))
                val preparacion = cursor.getString(cursor.getColumnIndexOrThrow(COL_PREPARACION))

                var valoresReceta = RecetaDatos(
                    idReceta,
                    nombreUsuario,
                    nombreReceta,
                    tiempo,
                    presupuesto,
                    preparacion,
                    null,
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGEN_URI)) // Nueva línea

                )
                recetasDatos.add(valoresReceta)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db.close()
        }
        return recetasDatos
    }
    fun optenerLosIngredientesPorIdRecetas(idReceta: Int): MutableList<Ingrediente> {
        val ingredientes = mutableListOf<Ingrediente>()
        val db = readableDatabase
        val query = """
    SELECT DISTINCT
        i.$COL_ID_INGREDIENTE,
        i.$COL_NOMBRE,
        i.$COL_PRECIO,
        ri.$COL_CANTIDAD,
        ri.$COL_UNIDAD
    FROM $NOMBRE_TABLA_RECETA r, $NOMBRE_TABLA_RECETAINGREDIENTES ri, $NOMBRE_TABLA_INGREDIENTE i
    WHERE ri.$COL_RECETA_ID = ? AND ri.$COL_INGREDIENTE_ID = i.$COL_ID_INGREDIENTE
    """
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(query, arrayOf(idReceta.toString()))

            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_INGREDIENTE))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE))
                val precio = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRECIO))
                val cantidad = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_CANTIDAD))
                val unidad = cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIDAD))
                val valoresReceta = Ingrediente(
                    id,
                    nombre,
                    precio,
                    cantidad,
                    unidad
                )
                ingredientes.add(valoresReceta)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db.close()
        }
        return ingredientes
    }


    @SuppressLint("Range")
    fun traerCalificacionesDeReceta(id: Int): List<CalificacionDatos> {
        val calificaciones = mutableListOf<CalificacionDatos>()
        val db = readableDatabase
        val columnas = arrayOf(
            "$NOMBRE_TABLA_USUARIO.$COL_NOMBRE_USUARIO",
            "$NOMBRE_TABLA_CALIFICACIONES.$COL_PUNTUACION",
            "$NOMBRE_TABLA_CALIFICACIONES.$COL_COMENTARIO"
        )
        val whereClause = "$COL_RECETA_ID_CALIFICACION = ?"
        val whereArgs = arrayOf(id.toString())

        val query = "SELECT ${columnas.joinToString()} " +
                "FROM $NOMBRE_TABLA_CALIFICACIONES " +
                "INNER JOIN $NOMBRE_TABLA_USUARIO " +
                "ON $NOMBRE_TABLA_CALIFICACIONES.$COL_USUARIO_ID_CALIFICACION = " +
                "$NOMBRE_TABLA_USUARIO.$COL_ID_USUARIO " +
                "WHERE $whereClause"

        val cursor: Cursor? = db.rawQuery(query, whereArgs)

        cursor?.use { cursor ->
            while (cursor.moveToNext()) {
                val nombreUsuario = cursor.getString(cursor.getColumnIndex(COL_NOMBRE_USUARIO))
                val puntuacion = cursor.getInt(cursor.getColumnIndex(COL_PUNTUACION))
                val comentario = cursor.getString(cursor.getColumnIndex(COL_COMENTARIO))
                val calificacion = CalificacionDatos(nombreUsuario, puntuacion, comentario)
                calificaciones.add(calificacion)
            }
        }
        cursor?.close()
        db.close()

        return calificaciones
    }
    fun agregarCalificacion(calificacion: Calificacion): Long {
        val db = writableDatabase
        val valoresInsert = ContentValues().apply {
            put(COL_USUARIO_ID_CALIFICACION,calificacion.idUsuario)
            put(COL_RECETA_ID_CALIFICACION, calificacion.idReceta)
            put(COL_PUNTUACION, calificacion.puntuacion)
            put(COL_COMENTARIO, calificacion.comentario)
        }
        val filas = db.insert(NOMBRE_TABLA_CALIFICACIONES, null, valoresInsert)
        db.close()
        return filas
    }

    fun traerNombreDeUsuario(idUsuario: Int): String? {
        val db = readableDatabase
        var nombreUsuario: String? = null

        val query = "SELECT $COL_NOMBRE_USUARIO FROM $NOMBRE_TABLA_USUARIO WHERE $COL_ID_USUARIO = ?"

        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, arrayOf(idUsuario.toString()))
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE_USUARIO))
                }
                cursor.close()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return nombreUsuario
    }
    fun  traerUsuario2(idUsuario: Int): Usuario? {
        val db = readableDatabase
        val columnas = arrayOf(COL_ID_USUARIO, COL_CORREO, COL_CONTRASENA, COL_NOMBRE_USUARIO)
        val selection = "$COL_ID_USUARIO = ?"
        val selectionArgs = arrayOf(idUsuario.toString())
        val cursor = db.query(
            NOMBRE_TABLA_USUARIO,
            columnas,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_USUARIO))
            val correo = cursor.getString(cursor.getColumnIndexOrThrow(COL_CORREO))
            val contrasena = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTRASENA))
            val nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE_USUARIO))
            usuario = Usuario(id, correo, contrasena, nombreUsuario)
        }
        cursor.close()
        db.close()
        return usuario
    }
    fun actualizarCorreo(correoActual: String, nuevoCorreo: String): Int {
        val db = writableDatabase
        val valoresUpdate = ContentValues().apply {
            put(COL_CORREO, nuevoCorreo)
        }
        val filasAfectadas = db.update(NOMBRE_TABLA_USUARIO, valoresUpdate, "$COL_CORREO = ?", arrayOf(correoActual))
        db.close()
        return filasAfectadas
    }
    fun eliminarUsuario(idUsuario: Int): Boolean {
        val db = writableDatabase
        val whereClauseUsuario = "$COL_ID_USUARIO = ?"
        val whereArgsUsuario = arrayOf(idUsuario.toString())
        val filasEliminadasUsuario = db.delete(NOMBRE_TABLA_USUARIO, whereClauseUsuario, whereArgsUsuario)

        // También eliminamos cualquier otra información relacionada (ej. recetas del usuario, seguidores, calificaciones, etc.)
        val whereClauseRecetas = "$COL_USUARIO_ID = ?"
        val whereArgsRecetas = arrayOf(idUsuario.toString())
        val filasEliminadasRecetas = db.delete(NOMBRE_TABLA_RECETA, whereClauseRecetas, whereArgsRecetas)

        val whereClauseSeguidor = "$COL_USUARIO_ID_SEGUIDOR = ? OR $COL_SEGUIDO_ID = ?"
        val whereArgsSeguidor = arrayOf(idUsuario.toString(), idUsuario.toString())
        val filasEliminadasSeguidor = db.delete(NOMBRE_TABLA_SEGUIDOR, whereClauseSeguidor, whereArgsSeguidor)

        val whereClauseCalificaciones = "$COL_USUARIO_ID_CALIFICACION = ?"
        val whereArgsCalificaciones = arrayOf(idUsuario.toString())
        val filasEliminadasCalificaciones = db.delete(NOMBRE_TABLA_CALIFICACIONES, whereClauseCalificaciones, whereArgsCalificaciones)

        db.close()

        // Retornamos true si se eliminó al menos una fila en alguna de las tablas relacionadas
        return filasEliminadasUsuario > 0 || filasEliminadasRecetas > 0 || filasEliminadasSeguidor > 0 || filasEliminadasCalificaciones > 0
    }

    fun eliminarReceta(idReceta: Int): Long {
        val db = writableDatabase
        var result: Long = -1
        try {
            result = db.delete(NOMBRE_TABLA_RECETA, "$COL_ID_RECETA = ?", arrayOf(idReceta.toString())).toLong()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return result
    }
    fun eliminarIngredientes(idReceta: Int): Long {
        val db = writableDatabase
        var result: Long = -1

        try {
            result = db.delete(NOMBRE_TABLA_RECETAINGREDIENTES, "$COL_RECETA_ID = ?", arrayOf(idReceta.toString())).toLong()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return result
    }
    fun eliminarCalificaciones(idReceta: Int): Long {
        val db = writableDatabase
        var result: Long = -1

        try {
            result = db.delete(NOMBRE_TABLA_CALIFICACIONES, "$COL_RECETA_ID_CALIFICACION = ?", arrayOf(idReceta.toString())).toLong()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return result
    }

    fun obtenerReceta(idReceta: Int): Receta? {
        val db = readableDatabase
        var receta: Receta? = null

        val query = """
            SELECT 
                $COL_ID_RECETA, 
                $COL_USUARIO_ID,
                $COL_NOMBRE_RECETA, 
                $COL_TIEMPO, 
                $COL_PRESUPUESTO, 
                $COL_PREPARACION
            FROM $NOMBRE_TABLA_RECETA 
            WHERE $COL_ID_RECETA = ?
        """.trimIndent()

        try {
            val cursor = db.rawQuery(query, arrayOf(idReceta.toString()))

            if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_RECETA))
                val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USUARIO_ID))
                val nombreReceta = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE_RECETA))
                val tiempo = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIEMPO))
                val presupuesto = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRESUPUESTO))
                val preparacion = cursor.getString(cursor.getColumnIndexOrThrow(COL_PREPARACION))

                receta = Receta(id, idUsuario, nombreReceta, tiempo, presupuesto, preparacion)
            }

            cursor?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return receta
    }

    fun modificarReceta(receta: Receta): Int {
        val db = writableDatabase
        val valoresUpdate = ContentValues().apply {
            put(COL_USUARIO_ID, receta.idUsuario)
            put(COL_NOMBRE_RECETA, receta.nombreReceta)
            put(COL_TIEMPO, receta.tiempo)
            put(COL_PRESUPUESTO, receta.presupuseto)
            put(COL_PREPARACION, receta.preparacion)
            put(COL_IMAGEN_URI, receta.imagenUri)
        }
        val filasAfectadas = db.update(NOMBRE_TABLA_RECETA, valoresUpdate, "$COL_ID_RECETA = ?", arrayOf(receta.id.toString()))
        db.close()
        return filasAfectadas
    }

    fun seguirUsuario(idSeguidor: String, idSeguido: Int): Boolean {
        val db = writableDatabase
        val valoresInsert = ContentValues().apply {
            put(COL_USUARIO_ID_SEGUIDOR, idSeguidor)
            put(COL_SEGUIDO_ID, idSeguido)
        }
        val resultado = db.insert(NOMBRE_TABLA_SEGUIDOR, null, valoresInsert)
        db.close()
        return resultado != -1L  // Retorna true si se insertó correctamente, false si hubo algún error
    }
    fun traerUsuarioPorNombre(nombreUsuario: String): Usuario? {
        val db = readableDatabase
        val columnas = arrayOf(COL_ID_USUARIO, COL_CORREO, COL_CONTRASENA, COL_NOMBRE_USUARIO)
        val selection = "$COL_NOMBRE_USUARIO = ?"
        val selectionArgs = arrayOf(nombreUsuario)
        val cursor = db.query(
            NOMBRE_TABLA_USUARIO,
            columnas,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_USUARIO))
            val correo = cursor.getString(cursor.getColumnIndexOrThrow(COL_CORREO))
            val contrasena = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTRASENA))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE_USUARIO))
            usuario = Usuario(id, correo, contrasena, nombre)
        }
        cursor.close()
        db.close()
        return usuario
    }


    fun buscarRecetasPorIngredientes(idIngredientes: Int): List<RecetaDatos> {
        val db = this.readableDatabase
        val recetas = mutableListOf<RecetaDatos>()

        if (idIngredientes<0) return recetas
        val query = """
                    SELECT 
                        r.$COL_ID_RECETA, 
                        u.$COL_NOMBRE_USUARIO, 
                        r.$COL_NOMBRE_RECETA, 
                        r.$COL_TIEMPO, 
                        r.$COL_PRESUPUESTO, 
                        r.$COL_PREPARACION,
                        r.$COL_IMAGEN_URI
                    FROM $NOMBRE_TABLA_RECETA r, $NOMBRE_TABLA_USUARIO u, $NOMBRE_TABLA_RECETAINGREDIENTES ri
                    WHERE $COL_INGREDIENTE_ID = ?  
                        AND ri.$COL_RECETA_ID = r.$COL_ID_RECETA 
                        AND u.$COL_ID_USUARIO = r.$COL_USUARIO_ID """

        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(query, arrayOf(idIngredientes.toString()))

            while (cursor.moveToNext()) {
                val idReceta = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_RECETA))
                val nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE_USUARIO))
                val nombreReceta = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE_RECETA))
                val tiempo = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIEMPO))
                val presupuesto = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRESUPUESTO))
                val preparacion = cursor.getString(cursor.getColumnIndexOrThrow(COL_PREPARACION))

                var valoresReceta = RecetaDatos(
                    idReceta,
                    nombreUsuario,
                    nombreReceta,
                    tiempo,
                    presupuesto,
                    preparacion,
                    null ,
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGEN_URI)) // Nueva línea

                )
                recetas.add(valoresReceta)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db.close()
        }
        return recetas
    }

    fun eliminarIngrediente(idIngrediente: Int): Long {
        val db = writableDatabase
        var result: Long = -1

        try {
            result = db.delete(NOMBRE_TABLA_RECETAINGREDIENTES, "$COL_INGREDIENTE_ID = ?", arrayOf(idIngrediente.toString())).toLong()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return result
    }
    fun editarIngrediente(ingrediente: Ingrediente): Int {
        val db = writableDatabase
        val valoresUpdate = ContentValues().apply {
            put(COL_CANTIDAD, ingrediente.cantidad)
        }
        val filasAfectadas = db.update(NOMBRE_TABLA_RECETAINGREDIENTES, valoresUpdate, "$COL_INGREDIENTE_ID = ?", arrayOf(ingrediente.id.toString()))
        db.close()
        return filasAfectadas
    }

    fun agregarIngrediente(idReceta: Int, ingrediente: Ingrediente): Long {
        val db = writableDatabase
        var resultado: Long = -1

        try {
            db.beginTransaction()

            val contentValues = ContentValues().apply {
                put(COL_RECETA_ID, idReceta)
                put(COL_INGREDIENTE_ID, ingrediente.id)
                put(COL_CANTIDAD, ingrediente.cantidad)
                put(COL_UNIDAD, ingrediente.unidad)
            }
            val insertResult = db.insert(NOMBRE_TABLA_RECETAINGREDIENTES, null, contentValues)
            if (insertResult == -1L) {
                throw SQLException("Error al insertar ingredientes")
            }
            db.setTransactionSuccessful()
            resultado = 1
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }

        return resultado
    }

    fun buscarRecetasPorPresupuesto(minimo: Double, maximo:Double): List<RecetaDatos> {
        val db = this.readableDatabase
        val recetas = mutableListOf<RecetaDatos>()

        val query = """
                    SELECT 
                        r.$COL_ID_RECETA, 
                        u.$COL_NOMBRE_USUARIO, 
                        r.$COL_NOMBRE_RECETA, 
                        r.$COL_TIEMPO, 
                        r.$COL_PRESUPUESTO, 
                        r.$COL_PREPARACION,
                        r.$COL_IMAGEN_URI
                    FROM $NOMBRE_TABLA_RECETA r, $NOMBRE_TABLA_USUARIO u, $NOMBRE_TABLA_RECETAINGREDIENTES ri
                    WHERE r.$COL_PRESUPUESTO >= ? 
                     AND r.$COL_PRESUPUESTO <= ?
                        AND ri.$COL_RECETA_ID = r.$COL_ID_RECETA 
                        AND u.$COL_ID_USUARIO = r.$COL_USUARIO_ID """

        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(query, arrayOf(minimo.toString(),maximo.toString()))

            while (cursor.moveToNext()) {
                val idReceta = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_RECETA))
                val nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE_USUARIO))
                val nombreReceta = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE_RECETA))
                val tiempo = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIEMPO))
                val presupuesto = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRESUPUESTO))
                val preparacion = cursor.getString(cursor.getColumnIndexOrThrow(COL_PREPARACION))

                var valoresReceta = RecetaDatos(
                    idReceta,
                    nombreUsuario,
                    nombreReceta,
                    tiempo,
                    presupuesto,
                    preparacion,
                    null,
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGEN_URI)) // Nueva línea

                )
                recetas.add(valoresReceta)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db.close()
        }
        return recetas
    }

    fun actualizarNombreUsuario(idUsuario: Int, nuevoNombre: String): Int {
        val db = writableDatabase
        val valoresUpdate = ContentValues().apply {
            put(COL_NOMBRE_USUARIO, nuevoNombre)
        }
        val filasAfectadas = db.update(NOMBRE_TABLA_USUARIO, valoresUpdate, "$COL_ID_USUARIO = ?", arrayOf(idUsuario.toString()))
        db.close()
        return filasAfectadas
    }


}