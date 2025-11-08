package com.example.organizer11.ui.add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer11.R

class IconAdapter(
    private val iconList: List<Int>, // Lista de IDs de drawable (ej: R.drawable.ic_bag)
    private val onIconClick: (Int) -> Unit
) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    inner class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val icon: ImageView = itemView.findViewById(R.id.image_icon)
        init {
            itemView.setOnClickListener {
                onIconClick(iconList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_icon, parent, false)
        return IconViewHolder(view)
    }

    override fun getItemCount() = iconList.size

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.icon.setImageResource(iconList[position])
    }
}