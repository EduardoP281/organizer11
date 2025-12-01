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
import com.example.organizer11.utils.NotificationScheduler
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AddReminderFragment : Fragment() {

    private val viewModel: ReminderViewModel by viewModels {
        ReminderViewModelFactory((requireActivity().application as OrganizerApplication).repository)
    }

    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var tvDateStart: TextView
    private lateinit var tvTime: TextView
    private lateinit var cardIconSelector: MaterialCardView
    private lateinit var ivSelectedIcon: ImageView

    // VARIABLES INTERNAS PARA GUARDAR DATOS PUROS
    private var finalDateString: String = ""
    private var finalTimeString: String = ""
    private var selectedIconResId: Int = R.drawable.ic_list

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_reminder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etTitle = view.findViewById(R.id.et_title)
        etDescription = view.findViewById(R.id.et_description)
        tvDateStart = view.findViewById(R.id.tv_date_start)
        tvTime = view.findViewById(R.id.tv_time)
        cardIconSelector = view.findViewById(R.id.card_icon_selector)
        ivSelectedIcon = view.findViewById(R.id.iv_selected_icon)
        val btnBack: ImageButton = view.findViewById(R.id.btn_back)
        val btnAdd: MaterialButton = view.findViewById(R.id.btn_add_reminder)

        // 1. Configurar fecha inicial (HOY)
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        updateDateLabel(today)

        // 2. Configurar hora inicial (12:00 PM)
        updateTimeLabel(12, 0)

        btnBack.setOnClickListener { findNavController().popBackStack() }

        cardIconSelector.setOnClickListener { IconPickerFragment().show(childFragmentManager, "IconPicker") }
        childFragmentManager.setFragmentResultListener("icon_picker_request", this) { _, bundle ->
            selectedIconResId = bundle.getInt("selected_icon")
            ivSelectedIcon.setImageResource(selectedIconResId)
        }
        ivSelectedIcon.setImageResource(selectedIconResId)

        tvDateStart.setOnClickListener { showDatePicker() }
        tvTime.setOnClickListener { showTimePicker() }
        btnAdd.setOnClickListener { saveReminder() }
    }

    private fun showDatePicker() {
        val constraintsBuilder = CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Seleccionar fecha")
            .setCalendarConstraints(constraintsBuilder.build())
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            updateDateLabel(selection)
        }
        datePicker.show(childFragmentManager, "DatePicker")
    }

    private fun updateDateLabel(timestamp: Long) {
        val date = Date(timestamp)
        // Usamos UTC para visualizar para que no reste un día
        val formatVisual = SimpleDateFormat("dd MMMM, yyyy", Locale("es", "ES"))
        formatVisual.timeZone = TimeZone.getTimeZone("UTC")
        tvDateStart.text = formatVisual.format(date)

        // Guardamos en la variable interna exactamente el mismo string
        finalDateString = formatVisual.format(date)
    }

    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Seleccionar hora límite")
            .build()

        picker.addOnPositiveButtonClickListener {
            updateTimeLabel(picker.hour, picker.minute)
        }
        picker.show(childFragmentManager, "TimePicker")
    }

    private fun updateTimeLabel(hour: Int, minute: Int) {
        // Usamos Calendar para manejar la hora local correctamente
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        // Forzamos Locale ES para que guarde "p. m." o "PM" consistente
        val format = SimpleDateFormat("hh:mm a", Locale("es", "ES"))
        finalTimeString = format.format(calendar.time)

        tvTime.text = finalTimeString
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
            startDate = finalDateString, // Usamos la variable interna
            endDate = finalDateString,
            dueTime = finalTimeString,   // Usamos la variable interna
            iconResId = selectedIconResId
        )

        viewModel.insertReminder(newReminder)

        // Programar notificaciones
        NotificationScheduler.scheduleNotifications(requireContext(), newReminder)

        findNavController().popBackStack()
    }
}