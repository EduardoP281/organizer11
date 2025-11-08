package com.example.organizer11.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Convertimos la data class en una tabla de base de datos
@Entity(tableName = "reminders_table")
data class Reminder(
    @PrimaryKey(autoGenerate = true) // El ID se genera solo
    val id: Int = 0, // Damos un valor por defecto
    val title: String,
    val description: String?,
    val dueDate: String,
    val iconResId: Int
)

// NOTA: Eliminamos 'status' por ahora para simplificar.
// Podemos añadirlo de nuevo más tarde.