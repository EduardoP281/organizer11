package com.example.organizer11.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.organizer11.R
import com.example.organizer11.data.model.Reminder
import com.example.organizer11.viewmodel.ReminderViewModel
import com.example.organizer11.viewmodel.ReminderViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.android.material.textfield.TextInputEditText
import com.example.organizer11.OrganizerApplication
// ▼▼▼ IMPORTANTE: Importar el planificador de notificaciones ▼▼▼
import com.example.organizer11.utils.NotificationScheduler
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddReminderFragment : Fragment() {

    private val viewModel: ReminderViewModel by viewModels {
        ReminderViewModelFactory((requireActivity().application as OrganizerApplication).repository)
    }

    // Vistas
    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var tvDateStart: TextView
    private lateinit var tvDateEnd: TextView
    private lateinit var tvTime: TextView
    private lateinit var cardIconSelector: MaterialCardView
    private lateinit var ivSelectedIcon: ImageView

    // Datos a guardar
    private var selectedStartDate: String = ""
    private var selectedEndDate: String = ""
    private var selectedTime: String = ""
    private var selectedIconResId: Int = R.drawable.ic_list

    private var startDateTimestamp: Long = 0
    private var endDateTimestamp: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_reminder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Encontrar vistas
        etTitle = view.findViewById(R.id.et_title)
        etDescription = view.findViewById(R.id.et_description)
        tvDateStart = view.findViewById(R.id.tv_date_start)
        tvDateEnd = view.findViewById(R.id.tv_date_end)
        tvTime = view.findViewById(R.id.tv_time)
        cardIconSelector = view.findViewById(R.id.card_icon_selector)
        ivSelectedIcon = view.findViewById(R.id.iv_selected_icon)
        val btnBack: ImageButton = view.findViewById(R.id.btn_back)
        val btnAdd: MaterialButton = view.findViewById(R.id.btn_add_reminder)

        // Inicializar fechas
        val todayTimestamp = MaterialDatePicker.todayInUtcMilliseconds()
        updateDateLabels(todayTimestamp, isStartDate = true)
        updateDateLabels(todayTimestamp, isStartDate = false)

        // Inicializar hora por defecto
        selectedTime = "12:00 PM"
        tvTime.text = selectedTime

        // Listeners
        btnBack.setOnClickListener { findNavController().popBackStack() }

        cardIconSelector.setOnClickListener { IconPickerFragment().show(childFragmentManager, "IconPicker") }

        childFragmentManager.setFragmentResultListener("icon_picker_request", this) { _, bundle ->
            selectedIconResId = bundle.getInt("selected_icon")
            ivSelectedIcon.setImageResource(selectedIconResId)
        }
        ivSelectedIcon.setImageResource(selectedIconResId)

        tvDateStart.setOnClickListener { showDatePicker(isStartDate = true) }
        tvDateEnd.setOnClickListener { showDatePicker(isStartDate = false) }

        tvTime.setOnClickListener { showTimePicker() }

        btnAdd.setOnClickListener { saveReminder() }
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val constraintsBuilder = CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Seleccionar fecha")
            .setCalendarConstraints(constraintsBuilder.build())
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            updateDateLabels(selection, isStartDate)
        }
        datePicker.show(childFragmentManager, "DatePicker")
    }

    private fun updateDateLabels(timestamp: Long, isStartDate: Boolean) {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd MMMM, yyyy", Locale("es", "ES"))
        val formattedDate = format.format(date)

        if (isStartDate) {
            startDateTimestamp = timestamp
            selectedStartDate = formattedDate
            tvDateStart.text = selectedStartDate
            if (endDateTimestamp != 0L && startDateTimestamp > endDateTimestamp) {
                updateDateLabels(startDateTimestamp, isStartDate = false)
            }
        } else {
            if (timestamp < startDateTimestamp) {
                Toast.makeText(context, "La fecha final no puede ser anterior al inicio.", Toast.LENGTH_SHORT).show()
                return
            }
            endDateTimestamp = timestamp
            selectedEndDate = formattedDate
            tvDateEnd.text = selectedEndDate
        }
    }

    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Seleccionar hora límite")
            .build()

        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute
            val amPm = if (hour < 12) "AM" else "PM"
            val hour12 = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
            val minuteString = String.format("%02d", minute)
            selectedTime = "$hour12:$minuteString $amPm"
            tvTime.text = selectedTime
        }
        picker.show(childFragmentManager, "TimePicker")
    }

    private fun saveReminder() {
        val title = etTitle.text.toString()
        val description = etDescription.text.toString()

        if (title.isEmpty()) {
            etTitle.error = "El título no puede estar vacío"
            return
        }

        val newReminder = Reminder(
            title = title,
            description = description,
            startDate = selectedStartDate,
            endDate = selectedEndDate,
            dueTime = selectedTime,
            iconResId = selectedIconResId
        )

        // 1. Guardar en Base de Datos
        viewModel.insertReminder(newReminder)

        // 2. Programar Notificaciones (ESTA ES LA PARTE NUEVA)
        // Esto calcula los tiempos y le dice a Android que lance las alertas
        NotificationScheduler.scheduleNotifications(requireContext(), newReminder)

        findNavController().popBackStack()
    }
}