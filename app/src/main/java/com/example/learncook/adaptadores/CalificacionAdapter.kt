package com.example.learncook.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.learncook.R
import com.example.learncook.poko.CalificacionDatos

class CalificacionAdapter (val calificaciones: List<CalificacionDatos>): RecyclerView.Adapter<CalificacionAdapter.ViewHolderCalficacion>(){
    class ViewHolderCalficacion(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvNombreUsuario: TextView = itemView.findViewById(R.id.tv_nombre_usuario)
        val tvCalificacion: TextView = itemView.findViewById(R.id.tv_calificacion)
        val tvComentario: TextView = itemView.findViewById(R.id.tv_comentario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCalficacion {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_calificaciones,parent,false)

        return ViewHolderCalficacion(itemView)
    }

    override fun getItemCount(): Int {
        return calificaciones.size
    }

    override fun onBindViewHolder(holder: ViewHolderCalficacion, position: Int) {
        val calificacion = calificaciones.get(position)
        holder.tvNombreUsuario.text =calificacion.nombreUsuario
        holder.tvCalificacion.text = ""+calificacion.puntuacion
        holder.tvComentario.text = calificacion.comentario
    }
}