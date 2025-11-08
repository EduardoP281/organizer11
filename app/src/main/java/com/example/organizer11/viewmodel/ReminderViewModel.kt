package com.example.organizer11.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.organizer11.data.database.AppDatabase
import com.example.organizer11.data.model.Reminder
import kotlinx.coroutines.launch

// 1. Esta es tu clase principal del ViewModel
class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val reminderDao = AppDatabase.getDatabase(application).reminderDao()

    val allReminders: LiveData<List<Reminder>> = reminderDao.getAllReminders().asLiveData()

    fun insertReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderDao.insertReminder(reminder)
        }
    }

    fun getReminder(id: Int): LiveData<Reminder> {
        return reminderDao.getReminderById(id).asLiveData()
    }
}


// ▼▼▼ ¡AQUÍ ESTÁ LA CLASE QUE FALTABA! ▼▼▼
// Pégala en el mismo archivo, pero FUERA de la otra clase.

class ReminderViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}