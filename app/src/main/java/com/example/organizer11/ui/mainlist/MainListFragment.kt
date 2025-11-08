package com.example.organizer11.ui.mainlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer11.R
import com.example.organizer11.data.model.Reminder
import com.example.organizer11.viewmodel.ReminderViewModel
import com.example.organizer11.viewmodel.ReminderViewModelFactory

class MainListFragment : Fragment() {

    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var rvReminders: RecyclerView

    // 1. Obtener el ViewModel
    private val viewModel: ReminderViewModel by viewModels {
        ReminderViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_list, container, false)

        rvReminders = view.findViewById(R.id.rv_reminders)

        // 2. Configurar el Adapter (inicialmente con una lista vacía)
        setupRecyclerView(emptyList())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 3. Observar la base de datos
        // Esto se ejecuta automáticamente cada vez que los datos cambian
        viewModel.allReminders.observe(viewLifecycleOwner) { reminders ->
            // 4. Actualizar el RecyclerView
            setupRecyclerView(reminders)
        }
    }

    private fun setupRecyclerView(reminders: List<Reminder>) {
        reminderAdapter = ReminderAdapter(requireContext(), reminders) { clickedReminder ->
            val action = MainListFragmentDirections
                .actionMainListFragmentToReminderDetailFragment(clickedReminder.id.toString())
            findNavController().navigate(action)
        }
        rvReminders.layoutManager = LinearLayoutManager(context)
        rvReminders.adapter = reminderAdapter
    }

    // 5. YA NO NECESITAMOS createDummyData()
}