package com.example.organizer11.ui.mainlist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer11.R
import com.example.organizer11.data.model.Reminder
import com.example.organizer11.utils.NotificationScheduler // Importamos tu Scheduler

interface ReminderClickListener {
    fun onDeleteClicked(reminder: Reminder)
    fun onImportanceChanged(reminder: Reminder, newImportance: Int)
    fun onStarredClicked(reminder: Reminder)
    fun onItemClicked(reminder: Reminder)
}

class ReminderAdapter(
    private val listener: ReminderClickListener
) : ListAdapter<Reminder, ReminderAdapter.ReminderViewHolder>(ReminderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val starIcon: ImageView = itemView.findViewById(R.id.iv_star)
        private val icon: ImageView = itemView.findViewById(R.id.iv_icon)
        private val title: TextView = itemView.findViewById(R.id.tv_title)
        private val dueDate: TextView = itemView.findViewById(R.id.tv_due_date)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btn_delete)
        private val importanceContainer: FrameLayout = itemView.findViewById(R.id.importance_indicator_container)
        private val importanceCircle: View = itemView.findViewById(R.id.importance_indicator_circle)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClicked(getItem(position))
                }
            }
        }

        fun bind(reminder: Reminder) {
            title.text = reminder.title

            // ▼▼▼ LÓGICA DE TIEMPO FINALIZADO / VENCIMIENTO ▼▼▼
            // Usamos la función pública del Scheduler para obtener los milisegundos
            val dueMillis = NotificationScheduler.parseDateToMillis(reminder.endDate, reminder.dueTime)
            val now = System.currentTimeMillis()

            android.util.Log.d("DEBUG_TIME", "Titulo: ${reminder.title}")
            android.util.Log.d("DEBUG_TIME", "FechaString: ${reminder.endDate} ${reminder.dueTime}")
            android.util.Log.d("DEBUG_TIME", "MillisCalculados: $dueMillis vs Ahora: $now")

            if (dueMillis != -1L && now > dueMillis) {
                // CASO 1: El tiempo ya pasó
                dueDate.text = "Tiempo Finalizado"
                dueDate.setTextColor(Color.RED)
            } else {
                // CASO 2: Aún a tiempo
                dueDate.text = "Vence: ${reminder.endDate} - ${reminder.dueTime}"
                dueDate.setTextColor(Color.DKGRAY) // Gris oscuro
            }
            // ▲▲▲ FIN LÓGICA ▼▼▼

            icon.setImageResource(reminder.iconResId)
            starIcon.visibility = if (reminder.isStarred) View.VISIBLE else View.GONE

            // Lógica para establecer el color del círculo de importancia
            val backgroundDrawableRes = when (reminder.importance) {
                1 -> R.drawable.color_circle_medium
                2 -> R.drawable.color_circle_high
                else -> R.drawable.color_circle_normal
            }
            importanceCircle.setBackgroundResource(backgroundDrawableRes)

            // Asignar listeners a los botones
            deleteButton.setOnClickListener { listener.onDeleteClicked(reminder) }
            importanceContainer.setOnClickListener { showCustomMenu(it, reminder) }
        }

        private fun showCustomMenu(anchorView: View, reminder: Reminder) {
            val context = anchorView.context
            val inflater = LayoutInflater.from(context)
            val popupView = inflater.inflate(R.layout.popup_importance_menu, null)

            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            // Configurar iconos del menú popup
            val starButton: ImageView = popupView.findViewById(R.id.popup_star)
            starButton.setImageResource(if (reminder.isStarred) R.drawable.ic_star_filled else R.drawable.ic_star_outline)

            val checkNormal: ImageView = popupView.findViewById(R.id.popup_check_normal)
            val checkMedium: ImageView = popupView.findViewById(R.id.popup_check_medium)
            val checkHigh: ImageView = popupView.findViewById(R.id.popup_check_high)

            // Resetear visibilidad de los checks
            checkNormal.visibility = View.GONE
            checkMedium.visibility = View.GONE
            checkHigh.visibility = View.GONE

            // Mostrar el check donde corresponda
            when (reminder.importance) {
                0 -> checkNormal.visibility = View.VISIBLE
                1 -> checkMedium.visibility = View.VISIBLE
                2 -> checkHigh.visibility = View.VISIBLE
            }

            // Click listeners del popup
            popupView.findViewById<View>(R.id.popup_star_container).setOnClickListener {
                listener.onStarredClicked(reminder)
                popupWindow.dismiss()
            }
            popupView.findViewById<View>(R.id.popup_color_normal).setOnClickListener {
                listener.onImportanceChanged(reminder, 0)
                popupWindow.dismiss()
            }
            popupView.findViewById<View>(R.id.popup_color_medium).setOnClickListener {
                listener.onImportanceChanged(reminder, 1)
                popupWindow.dismiss()
            }
            popupView.findViewById<View>(R.id.popup_color_high).setOnClickListener {
                listener.onImportanceChanged(reminder, 2)
                popupWindow.dismiss()
            }

            popupWindow.showAsDropDown(anchorView)
        }
    }
}

private class ReminderDiffCallback : DiffUtil.ItemCallback<Reminder>() {
    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem == newItem
    }
}