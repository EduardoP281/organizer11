package com.example.organizer11.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer11.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class IconPickerFragment : BottomSheetDialogFragment() {

    // Lista de recursos de íconos que puedes añadir o cambiar
    private val iconList = listOf(
        R.drawable.ic_list,
        R.drawable.ic_work,
        R.drawable.ic_school,
        R.drawable.ic_shopping_cart,
        R.drawable.ic_health,
        R.drawable.ic_home,
        R.drawable.ic_travel,
        R.drawable.ic_fitness,
        R.drawable.ic_star,
        R.drawable.ic_favorite,
        R.drawable.ic_event,
        R.drawable.ic_pet,
        R.drawable.ic_gift,
        R.drawable.ic_book,
        R.drawable.ic_music
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos nuestro layout para la cuadrícula de íconos
        return inflater.inflate(R.layout.fragment_icon_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_icons)
        recyclerView.layoutManager = GridLayoutManager(context, 4) // Cuadrícula de 4 columnas
        recyclerView.adapter = IconAdapter(iconList) { selectedIconResId ->
            // Cuando se selecciona un ícono, enviamos el resultado de vuelta al AddReminderFragment
            val resultBundle = Bundle().apply {
                putInt("selected_icon", selectedIconResId)
            }
            parentFragmentManager.setFragmentResult("icon_picker_request", resultBundle)
            dismiss() // Cerramos el diálogo después de seleccionar
        }
    }

    // Adaptador interno para el RecyclerView de íconos
    private class IconAdapter(
        private val icons: List<Int>,
        private val onIconSelected: (Int) -> Unit
    ) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

        class IconViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.image_icon)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_icon, parent, false)
            return IconViewHolder(view)
        }

        override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
            val iconResId = icons[position]
            holder.imageView.setImageResource(iconResId)
            holder.itemView.setOnClickListener {
                onIconSelected(iconResId)
            }
        }

        override fun getItemCount() = icons.size
    }
}