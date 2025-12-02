package com.example.organizer11.ui.mainlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible // Importante para .isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper // Para el Swipe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer11.OrganizerApplication
import com.example.organizer11.R
import com.example.organizer11.data.model.Reminder
import com.example.organizer11.databinding.FragmentMainListBinding
import com.example.organizer11.viewmodel.ReminderViewModel
import com.example.organizer11.viewmodel.ReminderViewModelFactory
import com.google.android.material.snackbar.Snackbar // Para el mensaje "Deshacer"
import com.google.firebase.auth.FirebaseAuth // Para cerrar sesión

class MainListFragment : Fragment(), ReminderClickListener {

    private var _binding: FragmentMainListBinding? = null
    private val binding get() = _binding!!

    // Inyecta el ViewModel usando la Factory
// CAMBIA ESE BLOQUE POR ESTE:
    private val viewModel: ReminderViewModel by viewModels {
        // La Factory ahora pide 'Application', así que le damos solo eso.
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

        // 1. Configurar RecyclerView
        setupRecyclerView()

        // 2. Configurar Swipe to Delete (Deslizar para borrar)
        setupSwipeToDelete()

        // 3. Configurar Botón Logout
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            // Navegar al Login y borrar el historial para no poder volver atrás
            findNavController().navigate(R.id.action_mainListFragment_to_singInFragment)
        }

        // 4. Observar datos y manejar Estado Vacío (Lottie)
        viewModel.allReminders.observe(viewLifecycleOwner) { reminders ->
            // Actualizar la lista
            reminderAdapter.submitList(reminders)

            // Lógica de Lottie: Si la lista está vacía, mostramos la animación
            val isEmpty = reminders.isEmpty()
            binding.rvReminders.isVisible = !isEmpty // Si NO está vacía, muestra la lista
            binding.layoutEmptyState.isVisible = isEmpty // Si ESTÁ vacía, muestra la animación
        }
    }

    private fun setupRecyclerView() {
        reminderAdapter = ReminderAdapter(this)
        binding.rvReminders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReminders.adapter = reminderAdapter
    }

    // Función para manejar el deslizamiento
    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // No queremos mover ítems de lugar, solo deslizar
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Obtener el recordatorio de la posición deslizada
                val position = viewHolder.adapterPosition
                val reminder = reminderAdapter.currentList[position]

                // Borrar de la base de datos
                viewModel.deleteReminder(reminder)

                // Mostrar mensaje Snack con opción "Deshacer"
                Snackbar.make(binding.root, "Recordatorio eliminado", Snackbar.LENGTH_LONG)
                    .setAction("Deshacer") {
                        // Si el usuario toca deshacer, lo insertamos de nuevo
                        viewModel.insertReminder(reminder)
                    }.show()
            }
        }

        // Unir el ayudante de toque al RecyclerView
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvReminders)
    }

    // --- Implementación de la Interfaz ReminderClickListener ---

    override fun onDeleteClicked(reminder: Reminder) {
        viewModel.deleteReminder(reminder)
        // Opcional: Mostrar Snackbar aquí también
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