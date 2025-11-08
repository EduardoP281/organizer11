package com.example.organizer11.ui.mainlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer11.R
import com.example.organizer11.data.model.Reminder


class ReminderAdapter(
    private val context: Context,
    private val reminders: List<Reminder>,
    private val onItemClick: (Reminder) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.iv_icon)
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val dueDate: TextView = itemView.findViewById(R.id.tv_due_date)
        val status: TextView = itemView.findViewById(R.id.tv_status) // La encontramos, pero la ocultaremos

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(reminders[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reminders.size
    }

    // ▼▼▼ ESTA SECCIÓN ES LA QUE CAMBIÓ ▼▼▼
    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        // Obtenemos el recordatorio actual (el nuevo modelo)
        val reminder = reminders[position]

        // Asignamos los datos que sí tenemos
        holder.icon.setImageResource(reminder.iconResId)
        holder.title.text = reminder.title
        holder.dueDate.text = reminder.dueDate

        // Como el nuevo modelo 'Reminder.kt' no tiene 'status',
        // simplemente ocultamos la etiqueta de estado.
        holder.status.visibility = View.GONE

        // BORRAMOS toda la lógica 'when (reminder.status) { ... }'
        // porque 'reminder.status' ya no existe.
    }
}