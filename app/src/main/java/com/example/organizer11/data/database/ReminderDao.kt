package com.example.organizer11.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.organizer11.data.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    // Insertar (Si ya existe, lo ignora o reemplaza según prefieras, IGNORE es seguro)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReminder(reminder: Reminder)

    // Actualizar
    @Update
    suspend fun updateReminder(reminder: Reminder)

    // Borrar
    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    // Obtener todos para la lista principal
    @Query("SELECT * FROM reminders_table ORDER BY id DESC")
    fun getAllReminders(): Flow<List<Reminder>>

    // Obtener los destacados (Para tu pantalla de destacados)
    @Query("SELECT * FROM reminders_table WHERE isStarred = 1 ORDER BY id DESC")
    fun getStarredReminders(): Flow<List<Reminder>>

    // ▼▼▼ AQUÍ ESTABA EL ERROR ▼▼▼
    // Cambiamos 'id: Long' por 'id: Int' para que coincida con tu Modelo
    @Query("SELECT * FROM reminders_table WHERE id = :id")
    fun getReminderById(id: Int): Flow<Reminder>
}