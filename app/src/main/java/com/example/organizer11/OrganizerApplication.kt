package com.example.organizer11

import android.app.Application
import com.example.organizer11.data.database.AppDatabase
import com.example.organizer11.data.repository.ReminderRepository

/**
 * Clase de Aplicación personalizada para crear y mantener instancias únicas
 * de la base de datos y el repositorio a lo largo de toda la app.
 */
class OrganizerApplication : Application() {

    // Usamos 'by lazy' para que la base de datos y el repositorio
    // solo se creen la primera vez que se necesiten.

    // Crea una instancia única de la base de datos.
    val database by lazy { AppDatabase.getDatabase(this) }

    // Crea una instancia única del repositorio, pasándole el DAO de la base de datos.
    val repository by lazy { ReminderRepository(database.reminderDao()) }
}
