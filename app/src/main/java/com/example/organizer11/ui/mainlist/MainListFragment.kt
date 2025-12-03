package com.example.organizer11.ui.mainlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer11.R
import com.example.organizer11.data.model.Reminder
import com.example.organizer11.databinding.FragmentMainListBinding
import com.example.organizer11.viewmodel.ReminderViewModel
import com.example.organizer11.viewmodel.ReminderViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class MainListFragment : Fragment(), ReminderClickListener {

    private var _binding: FragmentMainListBinding? = null
    private val binding get() = _binding!!

    // Inicialización del ViewModel (CORRECTA)
    private val viewModel: ReminderViewModel by viewModels {
        ReminderViewModelFactory(requireActivity().application)
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
        setupSwipeToDelete()

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_mainListFragment_to_singInFragment)
        }

        viewModel.allReminders.observe(viewLifecycleOwner) { reminders ->
            reminderAdapter.submitList(reminders)
            binding.rvReminders.isVisible = reminders.isNotEmpty()
            binding.layoutEmptyState.isVisible = reminders.isEmpty()
        }
    }

    private fun setupRecyclerView() {
        reminderAdapter = ReminderAdapter(this)
        binding.rvReminders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReminders.adapter = reminderAdapter
    }

    private fun setupSwipeToDelete() {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val reminder = reminderAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteReminder(reminder)
                Snackbar.make(binding.root, "Eliminado", Snackbar.LENGTH_LONG)
                    .setAction("Deshacer") { viewModel.insertReminder(reminder) }.show()
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(binding.rvReminders)
    }

    override fun onDeleteClicked(reminder: Reminder) {
        viewModel.deleteReminder(reminder)
    }

    override fun onImportanceChanged(reminder: Reminder, newImportance: Int) {
        // En Firestore, usamos 'updateReminder'
        val updated = reminder.copy(importance = newImportance)
        viewModel.updateReminder(updated)
    }

    override fun onStarredClicked(reminder: Reminder) {
        val updated = reminder.copy(isStarred = !reminder.isStarred)
        viewModel.updateReminder(updated)
    }

    override fun onItemClicked(reminder: Reminder) {
        val action = MainListFragmentDirections.actionMainListFragmentToReminderDetailFragment(reminder.id)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}