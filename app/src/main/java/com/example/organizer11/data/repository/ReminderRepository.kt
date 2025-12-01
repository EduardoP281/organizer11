package com.example.organizer11.data.repository

import com.example.organizer11.data.database.ReminderDao
import com.example.organizer11.data.model.Reminder
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que gestiona el acceso a los datos de los recordatorios.
 * Es la única fuente de verdad para los datos de la app.
 */
class ReminderRepository(private val reminderDao: ReminderDao) {

    // Flujo para todos los recordatorios
    val allReminders: Flow<List<Reminder>> = reminderDao.getAllReminders()

    // Flujo para los recordatorios destacados
    val starredReminders: Flow<List<Reminder>> = reminderDao.getStarredReminders()

    suspend fun insert(reminder: Reminder) {
        reminderDao.insertReminder(reminder)
    }

    suspend fun update(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
    }

    suspend fun delete(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }

    // Asegúrate de tener esto en tu repositorio:
    fun getReminder(id: Int): Flow<Reminder> {
        return reminderDao.getReminderById(id)
    }
}