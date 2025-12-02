package com.example.organizer11.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders_table")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // ▼▼▼ NUEVO CAMPO: El dueño del recordatorio ▼▼▼
    val userId: String,
    // ▲▲▲

    val title: String,
    val description: String?,
    val startDate: String,
    val endDate: String,
    val dueTime: String,
    val iconResId: Int,
    val isStarred: Boolean = false,
    val importance: Int = 0
)