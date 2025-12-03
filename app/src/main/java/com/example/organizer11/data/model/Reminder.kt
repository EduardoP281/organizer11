package com.example.organizer11.data.model

import com.google.firebase.firestore.PropertyName // <-- IMPORTANTE

data class Reminder(
    var id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String? = null,
    val startDate: String = "",
    val endDate: String = "",
    val dueTime: String = "",
    val iconResId: Int = 0,

    // ▼▼▼ ESTO SOLUCIONA EL PROBLEMA DE GUARDADO ▼▼▼
    @get:PropertyName("isStarred")
    val isStarred: Boolean = false,

    val importance: Int = 0
) {
    constructor() : this("", "", "", null, "", "", "", 0, false, 0)
}