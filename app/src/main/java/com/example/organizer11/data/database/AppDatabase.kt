package com.example.organizer11.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.organizer11.data.model.Reminder

// CAMBIO 1: Subimos la versión a 2
@Database(entities = [Reminder::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "organizer_database"
                )
                    // CAMBIO 2: Esta línea es MÁGICA.
                    // Le dice a la app: "Si cambias la estructura, borra la base vieja y empieza de cero".
                    // Esto evita el crash que estás teniendo.
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}