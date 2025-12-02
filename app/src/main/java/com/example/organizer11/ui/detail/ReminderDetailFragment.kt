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

    // CAMBIA ESE BLOQUE POR ESTE:
    private val viewModel: ReminderViewModel by viewModels {
        // La Factory ahora pide 'Application', así que le damos solo eso.
        ReminderViewModelFactory(requireActivity().application)
    }

    private val args: ReminderDetailFragmentArgs by navArgs()

    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    // private lateinit var tvStart: TextView // <-- ELIMINADO
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

        // Encontrar las vistas
        tvTitle = view.findViewById(R.id.tv_detail_title)
        tvDescription = view.findViewById(R.id.tv_detail_description)
        // tvStart = view.findViewById(R.id.tv_detail_start) // <-- ELIMINADO
        tvEnd = view.findViewById(R.id.tv_detail_end)
        tvTime = view.findViewById(R.id.tv_detail_time)

        val btnBack: ImageButton = view.findViewById(R.id.btn_back_detail)

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        loadReminderData()
    }

    private fun loadReminderData() {
        val reminderId = try {
            args.reminderId.toInt()
        } catch (e: NumberFormatException) {
            0
        }

        if (reminderId != 0) {
            viewModel.getReminder(reminderId).observe(viewLifecycleOwner) { reminder ->
                if (reminder != null) {
                    tvTitle.text = reminder.title
                    tvDescription.text = reminder.description ?: "Sin descripción"

                    // Solo mostramos la Fecha Final y la Hora
                    // tvStart.text = ... // <-- ELIMINADO
                    tvEnd.text = "Fecha límite: ${reminder.endDate}"
                    tvTime.text = "Hora límite: ${reminder.dueTime}"
                }
            }
        }
    }
}