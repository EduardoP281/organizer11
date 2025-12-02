package com.example.organizer11.data.model

// Quitamos @Entity y @PrimaryKey porque ya no usaremos Room
data class Reminder(
    var id: String = "", // CAMBIO CRÍTICO: Ahora es String y mutable
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
    // Constructor vacío necesario para que Firestore convierta los datos automáticamente
    constructor() : this("", "", "", null, "", "", "", 0, false, 0)
}