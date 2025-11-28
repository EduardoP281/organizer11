package com.example.organizer11.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.organizer11.data.model.Reminder
import com.example.organizer11.data.repository.ReminderRepository
import kotlinx.coroutines.launch

class ReminderViewModel(private val repository: ReminderRepository) : ViewModel() {

    // LiveData para todos los recordatorios
    val allReminders: LiveData<List<Reminder>> = repository.allReminders.asLiveData()

    // LiveData para los recordatorios destacados
    val starredReminders: LiveData<List<Reminder>> = repository.starredReminders.asLiveData()

    fun updateReminder(reminder: Reminder) = viewModelScope.launch {
        repository.update(reminder)
    }

    fun deleteReminder(reminder: Reminder) = viewModelScope.launch {
        repository.delete(reminder)
    }

    fun insertReminder(reminder: Reminder) = viewModelScope.launch {
        repository.insert(reminder)
    }

    // ▼▼▼ ESTA ES LA FUNCIÓN QUE FALTABA ▼▼▼
    fun getReminder(id: Int): LiveData<Reminder> {
        // Asumimos que tu repositorio tiene esta función.
        // Si te da error aquí, avísame para darte el código del Repository.
        return repository.getReminder(id).asLiveData()
    }
}

class ReminderViewModelFactory(private val repository: ReminderRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}