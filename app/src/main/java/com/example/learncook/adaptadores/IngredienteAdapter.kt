package com.example.learncook.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.learncook.R
import com.example.learncook.interfaces.ListenerRecycleIngrediente
import com.example.learncook.poko.Ingrediente

class IngredienteAdapter (val ingredientes: List<Ingrediente>, private val listener: ListenerRecycleIngrediente): RecyclerView.Adapter<IngredienteAdapter.ViewHolderIngrediente>(){
    inner class ViewHolderIngrediente(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvNombreIngrediente: TextView = itemView.findViewById(R.id.tv_nombre_ingrediente)
        val etCantidad: EditText = itemView.findViewById(R.id.tv_cantidad_ingrediente)
        val btnEliminar: Button = itemView.findViewById(R.id.btn_eliminar_ingrediente)
        val btnEditar: Button = itemView.findViewById(R.id.btn_editar_ingrediente)
        init {
            btnEliminar.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.clicEliminarIngrediente(ingredientes[position],position)
                }
            }
            btnEditar.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.clicEditarIngrediente(ingredientes[position],position, etCantidad)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderIngrediente {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_ingredientes,parent,false)

        return ViewHolderIngrediente(itemView)
    }

    override fun getItemCount(): Int {
        return ingredientes.size
    }

    override fun onBindViewHolder(holder: ViewHolderIngrediente, position: Int) {
        val ingrediente = ingredientes[position]
        holder.tvNombreIngrediente.text =ingrediente.nombre
        holder.etCantidad.setText(""+ingrediente.cantidad)
    }
}