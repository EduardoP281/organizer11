package com.example.organizer11.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.organizer11.data.model.Reminder
import com.example.organizer11.ui.worker.ReminderWorker
import java.text.SimpleDateFormat
import java.util.*

// Nota: Asegúrate de tener creada la clase 'ReminderReceiver' en este paquete o ajusta el import.
// Si no la tienes, te daré el código abajo.

object NotificationScheduler {

    private val TRIGGER_TIMES = listOf(
        Pair(2 * 24 * 60 * 60 * 1000L, "Faltan 2 días para:"),
        Pair(1 * 24 * 60 * 60 * 1000L, "Mañana tienes:"),
        Pair(6 * 60 * 60 * 1000L, "En 6 horas comienza:"),
        Pair(2 * 60 * 60 * 1000L, "En 2 horas comienza:"),
        Pair(30 * 60 * 1000L, "En 30 minutos:"),
        Pair(0L, "Ya es hora de:")
    )

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotifications(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Parseamos la fecha
        val eventTimeMillis = parseDateToMillis(reminder.endDate, reminder.dueTime)

        if (eventTimeMillis == -1L) return

        val currentTime = System.currentTimeMillis()

        TRIGGER_TIMES.forEachIndexed { index, (offsetMillis, prefix) ->

            val triggerAtMillis = eventTimeMillis - offsetMillis

            // Solo programamos si la fecha es en el futuro
            if (triggerAtMillis > currentTime) {

                // CORRECCIÓN AQUÍ:
                // Convertimos el ID String de Firestore a un Int usando hashCode()
                // Sumamos el index para que cada notificación del mismo recordatorio tenga ID distinto
                val uniqueNotificationId = reminder.id.hashCode() + index

                val intent = Intent(context, ReminderReceiver::class.java).apply {
                    putExtra("title", reminder.title)
                    putExtra("message", "$prefix ${reminder.title}")
                    putExtra("id", uniqueNotificationId) // Pasamos el Int corregido
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    uniqueNotificationId, // Usamos el Int corregido como RequestCode
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                        } else {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                        }
                    } else {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun parseDateToMillis(dateString: String, timeString: String): Long {
        return try {
            val format = SimpleDateFormat("dd MMMM, yyyy hh:mm a", Locale("es", "ES"))
            format.timeZone = TimeZone.getDefault()

            val cleanDate = dateString.trim()
            val cleanTime = timeString.trim()

            format.parse("$cleanDate $cleanTime")?.time ?: -1L
        } catch (e: Exception) {
            e.printStackTrace()
            -1L
        }
    }
}