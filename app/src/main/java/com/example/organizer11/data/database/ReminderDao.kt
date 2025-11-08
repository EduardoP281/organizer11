package com.example.organizer11.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.organizer11.data.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    // Insertar un nuevo recordatorio
    @Insert
    suspend fun insertReminder(reminder: Reminder)

    // Obtener todos los recordatorios (Flow se actualiza automáticamente)
    @Query("SELECT * FROM reminders_table ORDER BY id DESC")
    fun getAllReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders_table WHERE id = :id")
    fun getReminderById(id: Int): Flow<Reminder>
}