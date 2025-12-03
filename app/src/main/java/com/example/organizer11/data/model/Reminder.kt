package com.example.organizer11.data.model

// Clase de datos pura para Firestore (sin Room)
data class Reminder(
    var id: String = "", // ID de texto
    val userId: String = "",
    val title: String = "",
    val description: String? = null,
    val startDate: String = "",
    val endDate: String = "",
    val dueTime: String = "",
    val iconResId: Int = 0,
    val isStarred: Boolean = false,
    val importance: Int = 0
) {
    // Constructor vacío obligatorio para Firebase
    constructor() : this("", "", "", null, "", "", "", 0, false, 0)
}