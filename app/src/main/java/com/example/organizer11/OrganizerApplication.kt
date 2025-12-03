package com.example.organizer11

import android.app.Application

class OrganizerApplication : Application() {
    // Ya no necesitamos inicializar nada aquí. El Repositorio se encarga.
    override fun onCreate() {
        super.onCreate()
    }
}