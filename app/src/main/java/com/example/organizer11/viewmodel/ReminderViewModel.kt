package com.example.organizer11.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.organizer11.data.model.Reminder
import com.example.organizer11.data.repository.ReminderRepository
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    // Instanciamos el repositorio directamente (ya no necesita DAO)
    private val repository = ReminderRepository()

    val allReminders: LiveData<List<Reminder>> = repository.allReminders.asLiveData()
    val starredReminders: LiveData<List<Reminder>> = repository.starredReminders.asLiveData()

    fun insertReminder(reminder: Reminder) = viewModelScope.launch {
        repository.insert(reminder)
    }

    fun updateReminder(reminder: Reminder) = viewModelScope.launch {
        repository.update(reminder)
    }

    fun deleteReminder(reminder: Reminder) = viewModelScope.launch {
        repository.delete(reminder)
    }

    // CAMBIO: El ID ahora es String
    fun getReminder(id: String): LiveData<Reminder> {
        val result = MutableLiveData<Reminder>()
        viewModelScope.launch {
            val reminder = repository.getReminder(id)
            if (reminder != null) {
                result.postValue(reminder)
            }
        }
        return result
    }
}

class ReminderViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}