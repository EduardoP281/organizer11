package com.example.organizer11.data.repository

import com.example.organizer11.data.database.ReminderDao
import com.example.organizer11.data.model.Reminder
import com.google.firebase.auth.FirebaseAuth // <-- Importante
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val reminderDao: ReminderDao) {

    // Helper para obtener el ID actual de Firebase
    // Si no hay usuario (ej. logout), devuelve una cadena vacía para que no muestre datos de otros
    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // --- LIVEDATA / FLOWS ---

    // Ahora pasamos el 'currentUserId' al DAO
    val allReminders: Flow<List<Reminder>>
        get() = reminderDao.getAllReminders(currentUserId)

    val starredReminders: Flow<List<Reminder>>
        get() = reminderDao.getStarredReminders(currentUserId)

    // --- FUNCIONES SUSPENDIDAS ---

    suspend fun insert(reminder: Reminder) {
        reminderDao.insertReminder(reminder)
    }

    suspend fun update(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
    }

    suspend fun delete(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }

    fun getReminder(id: Int): Flow<Reminder> {
        return reminderDao.getReminderById(id)
    }
}