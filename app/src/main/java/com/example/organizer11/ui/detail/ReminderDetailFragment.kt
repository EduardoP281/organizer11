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

    // 1. Obtener el ViewModel
    private val viewModel: ReminderViewModel by viewModels {
        ReminderViewModelFactory(requireActivity().application)
    }

    // 2. Obtener los argumentos de navegación (el ID)
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

        // 3. Configurar el botón de retroceso
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // 4. Cargar los datos
        loadReminderData()
    }

    private fun loadReminderData() {
        // Convertimos el ID (que es String) a Int
        val reminderId = args.reminderId.toInt()

        // 5. Le pedimos al ViewModel que busque ese recordatorio
        viewModel.getReminder(reminderId).observe(viewLifecycleOwner) { reminder ->
            // 6. Cuando el ViewModel nos da el recordatorio, actualizamos la UI
            if (reminder != null) {
                tvTitle.text = reminder.title
                tvDescription.text = reminder.description
                tvDate.text = reminder.dueDate
            }
        }
    }
}