package com.example.learncook.adaptadores

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.learncook.R
import com.example.learncook.interfaces.ListenerRecycleReceta
import com.example.learncook.poko.RecetaDatos

class RecetaAdapter(
    private val recetas: List<RecetaDatos>,
    private val listener: ListenerRecycleReceta,
    private val unidadSeleccionada: String
) : RecyclerView.Adapter<RecetaAdapter.ViewHolderReceta>() {

    inner class ViewHolderReceta(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombreUsuario: TextView = itemView.findViewById(R.id.tv_nombre_usuario)
        val tvNombreReceta: TextView = itemView.findViewById(R.id.tv_titulo)
        val tvTiempo: TextView = itemView.findViewById(R.id.tv_tiempo)
        val tvIngredientes: TextView = itemView.findViewById(R.id.tv_ingredientes)
        val tvPresupuesto: TextView = itemView.findViewById(R.id.tv_presupuesto)
        val tvPreparacion: TextView = itemView.findViewById(R.id.tv_preparacion)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btn_delete)
        val btnCalificar: ImageButton = itemView.findViewById(R.id.calificar_receta)
        val btnCompartir: ImageButton = itemView.findViewById(R.id.btn_compartir)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btn_editar)
        val ivReceta: ImageView = itemView.findViewById(R.id.iv_receta)

        init {
            btnCalificar.setOnClickListener { listener.clicCalificarReceta(recetas[adapterPosition], adapterPosition) }
            btnCompartir.setOnClickListener { listener.clicCompartirReceta(recetas[adapterPosition], adapterPosition) }
            btnEliminar.setOnClickListener { listener.clicEliminarReceta(recetas[adapterPosition], adapterPosition) }
            btnEditar.setOnClickListener { listener.clicEditarReceta(recetas[adapterPosition], adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderReceta {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_recetas, parent, false)
        return ViewHolderReceta(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderReceta, position: Int) {
        val receta = recetas[position]

        holder.tvNombreUsuario.text = receta.nombreUsuario
        holder.tvNombreReceta.text = receta.nombreReceta
        holder.tvTiempo.text = receta.tiempo
        holder.tvPresupuesto.text = receta.presupuesto.toString()
        holder.tvPreparacion.text = receta.preparacion
        holder.tvIngredientes.text = receta.ingredientes?.joinToString("\n") {
            "${it.cantidad} $unidadSeleccionada - ${it.nombre}"
        } ?: "Sin ingredientes"
// Cargar imagen con Glide
        receta.imagenUri?.let { uriString ->
            try {
                Glide.with(holder.itemView.context)
                    .load(Uri.parse(uriString))
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .centerCrop()
                    .into(holder.ivReceta)
            } catch (e: Exception) {
                holder.ivReceta.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } ?: run {
            holder.ivReceta.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    override fun getItemCount(): Int = recetas.size
}