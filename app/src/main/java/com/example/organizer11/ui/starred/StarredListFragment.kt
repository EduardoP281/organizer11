package com.example.organizer11.ui.starred

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.organizer11.OrganizerApplication
import com.example.organizer11.data.model.Reminder
import com.example.organizer11.databinding.FragmentStarredListBinding
import com.example.organizer11.ui.mainlist.ReminderAdapter
import com.example.organizer11.ui.mainlist.ReminderClickListener
import com.example.organizer11.viewmodel.ReminderViewModel
import com.example.organizer11.viewmodel.ReminderViewModelFactory

class StarredListFragment : Fragment(), ReminderClickListener {

    private var _binding: FragmentStarredListBinding? = null
    private val binding get() = _binding!!

    // CAMBIA ESE BLOQUE POR ESTE:
    private val viewModel: ReminderViewModel by viewModels {
        // La Factory ahora solo necesita la 'application'
        ReminderViewModelFactory(requireActivity().application)
    }

    private lateinit var reminderAdapter: ReminderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStarredListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.starredReminders.observe(viewLifecycleOwner) { starredList ->
            reminderAdapter.submitList(starredList)

            // --- LÍNEA ACTUALIZADA ---
            binding.layoutNoStarredItems.isVisible = starredList.isEmpty()
            binding.rvStarredReminders.isVisible = starredList.isNotEmpty()
        }
    }

    private fun setupRecyclerView() {
        reminderAdapter = ReminderAdapter(this)
        binding.rvStarredReminders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStarredReminders.adapter = reminderAdapter
    }

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
        val action = StarredListFragmentDirections.actionStarredListFragmentToReminderDetailFragment(reminder.id.toString())
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
