package com.example.organizer11.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView // <-- CAMBIO AQUI
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.organizer11.R
import com.example.organizer11.data.model.Reminder
import com.example.organizer11.viewmodel.ReminderViewModel
import com.example.organizer11.viewmodel.ReminderViewModelFactory // <-- CAMBIO AQUI
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.example.organizer11.OrganizerApplication
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
    private lateinit var cardIconSelector: MaterialCardView
    private lateinit var ivSelectedIcon: ImageView // <-- CAMBIO AQUI

    // Datos a guardar
    private var selectedStartDate: String = "Hoy"
    private var selectedEndDate: String = "Sin fecha"
    private var selectedIconResId: Int = R.drawable.ic_list // Ícono por defecto

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_reminder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Encontrar vistas
        etTitle = view.findViewById(R.id.et_title)
        etDescription = view.findViewById(R.id.et_description)
        tvDateStart = view.findViewById(R.id.tv_date_start)
        tvDateEnd = view.findViewById(R.id.tv_date_end)
        cardIconSelector = view.findViewById(R.id.card_icon_selector)
        ivSelectedIcon = view.findViewById(R.id.iv_selected_icon) // <-- CAMBIO AQUI
        val btnBack: ImageButton = view.findViewById(R.id.btn_back)
        val btnAdd: MaterialButton = view.findViewById(R.id.btn_add_reminder)

        // Configurar listeners
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Listener para el selector de ícono
        cardIconSelector.setOnClickListener {
            IconPickerFragment().show(childFragmentManager, "IconPicker")
        }

        // Listener para el resultado del selector de ícono
        childFragmentManager.setFragmentResultListener("icon_picker_request", this) { _, bundle ->
            selectedIconResId = bundle.getInt("selected_icon")
            ivSelectedIcon.setImageResource(selectedIconResId) // <-- CAMBIO AQUI: Actualizar la UI
        }

        // Inicializar el ícono seleccionado en la UI
        ivSelectedIcon.setImageResource(selectedIconResId) // <-- CAMBIO AQUI

        // Listener para fecha de INICIO
        tvDateStart.setOnClickListener {
            showDatePicker(isStartDate = true)
        }

        // Listener para fecha de FIN
        tvDateEnd.setOnClickListener {
            showDatePicker(isStartDate = false)
        }

        // Configurar el botón "Añadir" para GUARDAR
        btnAdd.setOnClickListener {
            saveReminder()
        }
    }

    // Función de DatePicker (ahora maneja ambas fechas)
    private fun showDatePicker(isStartDate: Boolean = true) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Seleccionar fecha")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            // El 'selection' es un timestamp (Long). Lo convertimos a un formato legible.
            val date = Date(selection)
            val format = SimpleDateFormat("dd MMMM, yyyy", Locale("es", "ES"))
            val formattedDate = format.format(date)

            // Actualiza la variable Y el texto correctos
            if (isStartDate) {
                selectedStartDate = formattedDate
                tvDateStart.text = selectedStartDate
            } else {
                selectedEndDate = formattedDate
                tvDateEnd.text = selectedEndDate
            }
        }

        // Mostrar el DatePicker
        datePicker.show(childFragmentManager, "DatePicker")
    }

    private fun saveReminder() {
        val title = etTitle.text.toString()
        val description = etDescription.text.toString()

        // Validar que el título no esté vacío
        if (title.isEmpty()) {
            etTitle.error = "El título no puede estar vacío"
            return
        }

        // Crear el objeto Recordatorio
        val newReminder = Reminder(
            title = title,
            description = description,
            dueDate = selectedStartDate, // Usamos la fecha de inicio
            iconResId = selectedIconResId
        )

        // Usar el ViewModel para insertar en la base de datos
        viewModel.insertReminder(newReminder)

        // Volver a la pantalla principal
        findNavController().popBackStack()
    }
}