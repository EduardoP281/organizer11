package com.example.organizer11.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// 1. Cambia el nombre de la tabla a "reminders_table" para que coincida con tu DAO
@Entity(tableName = "reminders_table")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val dueDate: String,
    val iconResId: Int,
    val description: String?,
    val importance: Int = 0, // 0: Normal, 1: Medio, 2: Alto
    val isStarred: Boolean = false
)
