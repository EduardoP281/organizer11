package com.example.organizer11.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AddReminderFragment : Fragment() {

    private val viewModel: ReminderViewModel by viewModels {
        ReminderViewModelFactory(requireActivity().application)
    }

    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var tvDateStart: TextView
    // private lateinit var tvDateEnd: TextView // <-- ELIMINADO
    private lateinit var tvTime: TextView
    private lateinit var cardIconSelector: MaterialCardView
    private lateinit var ivSelectedIcon: ImageView

    // Datos a guardar
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private var selectedIconResId: Int = R.drawable.ic_list

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_reminder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etTitle = view.findViewById(R.id.et_title)
        etDescription = view.findViewById(R.id.et_description)
        tvDateStart = view.findViewById(R.id.tv_date_start)
        // tvDateEnd = view.findViewById(R.id.tv_date_end) // <-- ELIMINADO
        tvTime = view.findViewById(R.id.tv_time)
        cardIconSelector = view.findViewById(R.id.card_icon_selector)
        ivSelectedIcon = view.findViewById(R.id.iv_selected_icon)

        val btnBack: ImageButton = view.findViewById(R.id.btn_back)
        val btnAdd: MaterialButton = view.findViewById(R.id.btn_add_reminder)

        // Inicializar con la fecha de hoy
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        updateDateLabel(today)

        // Inicializar hora
        selectedTime = "12:00 PM"
        tvTime.text = selectedTime

        btnBack.setOnClickListener { findNavController().popBackStack() }

        cardIconSelector.setOnClickListener { IconPickerFragment().show(childFragmentManager, "IconPicker") }

        childFragmentManager.setFragmentResultListener("icon_picker_request", this) { _, bundle ->
            selectedIconResId = bundle.getInt("selected_icon")
            ivSelectedIcon.setImageResource(selectedIconResId)
        }
        ivSelectedIcon.setImageResource(selectedIconResId)

        // Solo un listener para la fecha
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
        val formatVisual = SimpleDateFormat("dd MMMM, yyyy", Locale("es", "ES"))
        formatVisual.timeZone = TimeZone.getTimeZone("UTC")
        selectedDate = formatVisual.format(date) // Guardamos la fecha
        tvDateStart.text = selectedDate
    }

    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Seleccionar hora límite")
            .build()

        picker.addOnPositiveButtonClickListener {
            val amPm = if (picker.hour < 12) "AM" else "PM"
            val hour12 = if (picker.hour > 12) picker.hour - 12 else if (picker.hour == 0) 12 else picker.hour
            val minuteString = String.format("%02d", picker.minute)
            selectedTime = "$hour12:$minuteString $amPm"
            tvTime.text = selectedTime
        }
        picker.show(childFragmentManager, "TimePicker")
    }

    private fun saveReminder() {
        val title = etTitle.text.toString()
        val desc = etDescription.text.toString()
        if (title.isEmpty()) { etTitle.error = "Requerido"; return }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Generamos ID
        val newId = FirebaseFirestore.getInstance().collection("users").document().id

        val newReminder = Reminder(
            id = newId,
            userId = userId,
            title = title,
            description = desc,
            // Guardamos la misma fecha en Start y End para compatibilidad
            startDate = selectedDate,
            endDate = selectedDate,
            dueTime = selectedTime,
            iconResId = selectedIconResId
        )

        viewModel.insertReminder(newReminder)
        NotificationScheduler.scheduleNotifications(requireContext(), newReminder)
        findNavController().popBackStack()
    }
}