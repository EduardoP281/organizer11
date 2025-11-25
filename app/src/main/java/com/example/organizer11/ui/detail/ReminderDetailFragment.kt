package com.example.organizer11.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.organizer11.OrganizerApplication
import com.example.organizer11.R
import com.example.organizer11.viewmodel.ReminderViewModel
import com.example.organizer11.viewmodel.ReminderViewModelFactory

class ReminderDetailFragment : Fragment() {

    // 1. Obtener el ViewModel (esto ya lo tenías bien)
    private val viewModel: ReminderViewModel by viewModels {
        ReminderViewModelFactory((requireActivity().application as OrganizerApplication).repository)
    }

    // 2. Obtener los argumentos de navegación (el ID del recordatorio)
    private val args: ReminderDetailFragmentArgs by navArgs()

    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvDate: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reminder_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Encontrar las vistas
        tvTitle = view.findViewById(R.id.tv_detail_title)
        tvDescription = view.findViewById(R.id.tv_detail_description)
        tvDate = view.findViewById(R.id.tv_detail_date)
        val btnBack: ImageButton = view.findViewById(R.id.btn_back_detail)

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // 3. Cargar los datos (forma corregida y eficiente)
        loadReminderData()
    }

    private fun loadReminderData() {
        // Convertimos el ID del argumento (que es String) a Long, porque así está en el modelo
        val reminderId = args.reminderId.toLong()

        // 4. Observamos la lista COMPLETA de recordatorios
        viewModel.allReminders.observe(viewLifecycleOwner) { reminders ->
            // 5. Buscamos en la lista el recordatorio que coincida con nuestro ID
            val reminder = reminders.find { it.id == reminderId }

            // 6. Si lo encontramos, actualizamos la UI
            if (reminder != null) {
                tvTitle.text = reminder.title
                tvDescription.text = reminder.description ?: "Sin descripción" // Usamos el operador Elvis para el campo opcional
                tvDate.text = reminder.dueDate
            }
        }
    }
}
