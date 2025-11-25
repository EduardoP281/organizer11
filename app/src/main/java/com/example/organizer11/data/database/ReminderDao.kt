package com.example.organizer11.data.database

import androidx.lifecycle.LiveData
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

    @Query("SELECT * FROM reminders_table ORDER BY importance DESC, id DESC")
    fun getAllReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders_table WHERE id = :id")
    fun getReminderById(id: Long): Flow<Reminder>

    // --- NUEVO MÉTODO ---
    @Query("SELECT * FROM reminders_table WHERE isStarred = 1 ORDER BY importance DESC, id DESC")
    fun getStarredReminders(): Flow<List<Reminder>>
}
