package com.example.organizer11.data.model

<<<<<<< Updated upstream
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
=======
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
>>>>>>> Stashed changes
    val isStarred: Boolean = false,

    val importance: Int = 0
<<<<<<< Updated upstream
)
=======
) {
    constructor() : this("", "", "", null, "", "", "", 0, false, 0)
}
>>>>>>> Stashed changes
