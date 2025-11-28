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
import com.example.organizer11.R
import com.example.organizer11.viewmodel.ReminderViewModel
import com.example.organizer11.viewmodel.ReminderViewModelFactory

class ReminderDetailFragment : Fragment() {

    // 1. Inicializamos el ViewModel correctamente
// CAMBIA ESE BLOQUE POR ESTE:
    private val viewModel: ReminderViewModel by viewModels {
        // Casteamos la application a 'OrganizerApplication' para poder acceder al '.repository'
        ReminderViewModelFactory((requireActivity().application as com.example.organizer11.OrganizerApplication).repository)
    }

    // 2. Obtener los argumentos (el ID)
    private val args: ReminderDetailFragmentArgs by navArgs()

    // 3. Declaramos las nuevas variables para las 3 fechas
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvStart: TextView
    private lateinit var tvEnd: TextView
    private lateinit var tvTime: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reminder_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 4. Encontrar las vistas (coinciden con el nuevo XML que te di)
        tvTitle = view.findViewById(R.id.tv_detail_title)
        tvDescription = view.findViewById(R.id.tv_detail_description)
        tvStart = view.findViewById(R.id.tv_detail_start) // Fecha Inicio
        tvEnd = view.findViewById(R.id.tv_detail_end)     // Fecha Fin
        tvTime = view.findViewById(R.id.tv_detail_time)   // Hora Límite

        val btnBack: ImageButton = view.findViewById(R.id.btn_back_detail)

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        loadReminderData()
    }

    private fun loadReminderData() {
        // Convertimos el ID a Int (porque en la BD es Int)
        val reminderId = args.reminderId.toInt()

        // 5. Usamos la función optimizada 'getReminder' que busca solo UNO por ID
        viewModel.getReminder(reminderId).observe(viewLifecycleOwner) { reminder ->
            if (reminder != null) {
                // 6. Asignamos los datos a la interfaz
                tvTitle.text = reminder.title
                tvDescription.text = reminder.description ?: "Sin descripción"

                // Formato visual para las fechas
                tvStart.text = "Inicia: ${reminder.startDate}"
                tvEnd.text = "Termina: ${reminder.endDate}"
                tvTime.text = "Hora límite: ${reminder.dueTime}"
            }
        }
    }
}