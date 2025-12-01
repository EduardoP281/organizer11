package com.example.organizer11.ui.mainlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.organizer11.OrganizerApplication
import com.example.organizer11.data.model.Reminder
import com.example.organizer11.databinding.FragmentMainListBinding
import com.example.organizer11.viewmodel.ReminderViewModel
import com.example.organizer11.viewmodel.ReminderViewModelFactory // <-- Importa la Factory

// 1. Implementa la interfaz del adapter (esto ya está bien)
class MainListFragment : Fragment(), ReminderClickListener {

    private var _binding: FragmentMainListBinding? = null
    private val binding get() = _binding!!

    // --- ▼▼▼ ¡ESTA ES LA LÍNEA MÁS IMPORTANTE A CAMBIAR! ▼▼▼ ---
    // Inyecta el ViewModel usando la Factory para pasarle el repositorio.
    private val viewModel: ReminderViewModel by viewModels {
        ReminderViewModelFactory((requireActivity().application as OrganizerApplication).repository)
    }

    private lateinit var reminderAdapter: ReminderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        // Observa los datos del ViewModel
        viewModel.allReminders.observe(viewLifecycleOwner) { reminders ->
            // Envía la lista actualizada al adapter
            reminderAdapter.submitList(reminders)
        }
    }

    private fun setupRecyclerView() {
        // Pasa 'this' como listener
        reminderAdapter = ReminderAdapter(this)
        binding.rvReminders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReminders.adapter = reminderAdapter
    }

    // 3. Implementa los métodos de la interfaz (todo este bloque ya está perfecto)
    override fun onDeleteClicked(reminder: Reminder) {
        viewModel.deleteReminder(reminder)
    }

    override fun onImportanceChanged(reminder: Reminder, newImportance: Int) {
        val updatedReminder = reminder.copy(importance = newImportance)
        viewModel.updateReminder(updatedReminder)
    }

    override fun onStarredClicked(reminder: Reminder) {
        val updatedReminder = reminder.copy(isStarred = !reminder.isStarred)
        viewModel.updateReminder(updatedReminder)
    }

    override fun onItemClicked(reminder: Reminder) {
        val action = MainListFragmentDirections
            .actionMainListFragmentToReminderDetailFragment(reminder.id.toString())
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
