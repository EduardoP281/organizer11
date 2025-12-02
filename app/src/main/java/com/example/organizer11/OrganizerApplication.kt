package com.example.organizer11

import android.app.Application
import com.example.organizer11.data.repository.ReminderRepository

class OrganizerApplication : Application() {
    // Ya no necesitamos "database" ni "dao".
    // El repositorio de Firestore se inicializa solo.
    val repository by lazy { ReminderRepository() }
}