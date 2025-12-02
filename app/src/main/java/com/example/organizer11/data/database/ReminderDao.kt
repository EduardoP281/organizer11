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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReminder(reminder: Reminder)

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    // ▼▼▼ FILTRAR POR USUARIO ▼▼▼
    @Query("SELECT * FROM reminders_table WHERE userId = :userId ORDER BY id DESC")
    fun getAllReminders(userId: String): Flow<List<Reminder>>

    // ▼▼▼ FILTRAR POR USUARIO Y DESTACADOS ▼▼▼
    @Query("SELECT * FROM reminders_table WHERE isStarred = 1 AND userId = :userId ORDER BY id DESC")
    fun getStarredReminders(userId: String): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders_table WHERE id = :id")
    fun getReminderById(id: Int): Flow<Reminder>
}