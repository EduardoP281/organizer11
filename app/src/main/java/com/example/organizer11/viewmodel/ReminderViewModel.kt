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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val reminderDao = AppDatabase.getDatabase(application).reminderDao()

    // Obtenemos el ID del usuario actual de Firebase
    private val currentUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"

    // Ahora filtramos las listas usando ese ID
    val allReminders: LiveData<List<Reminder>> = reminderDao.getAllReminders(currentUserId).asLiveData()
    val starredReminders: LiveData<List<Reminder>> = reminderDao.getStarredReminders(currentUserId).asLiveData()

    fun insertReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderDao.insertReminder(reminder)
        }
    }

    fun updateReminder(reminder: Reminder) = viewModelScope.launch {
        reminderDao.updateReminder(reminder)
    }

    fun deleteReminder(reminder: Reminder) = viewModelScope.launch {
        reminderDao.deleteReminder(reminder)
    }

    fun getReminder(id: Int): LiveData<Reminder> {
        return reminderDao.getReminderById(id).asLiveData()
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