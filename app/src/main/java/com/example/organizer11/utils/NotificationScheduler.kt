package com.example.organizer11.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.example.organizer11.data.model.Reminder
import java.text.SimpleDateFormat
import java.util.*

object NotificationScheduler {

    private val TRIGGER_TIMES = listOf(
        Pair(2 * 24 * 60 * 60 * 1000L, "Faltan 2 días para:"),
        Pair(1 * 24 * 60 * 60 * 1000L, "Mañana tienes:"),
        Pair(6 * 60 * 60 * 1000L, "En 6 horas comienza:"),
        Pair(1 * 60 * 60 * 1000L, "En 1 hora:"),
        Pair(0L, "Ya es hora de:")
    )

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotifications(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

<<<<<<< Updated upstream
        val eventTimeMillis = parseDateToMillis(reminder.startDate, reminder.dueTime)
=======
        // 1. Intentar convertir la fecha
        val eventTimeMillis = parseDateToMillis(reminder.endDate, reminder.dueTime)
>>>>>>> Stashed changes

        if (eventTimeMillis == -1L) {
            Toast.makeText(context, "Error: No se pudo leer la fecha de la alarma", Toast.LENGTH_LONG).show()
            return
        }

        // --- PRUEBA DE 10 SEGUNDOS (Para verificar permisos) ---
        try {
            val testTime = System.currentTimeMillis() + 10_000L
            scheduleAlarm(context, alarmManager, testTime, reminder, "Prueba de sistema", 99999)
            // Si ves este mensaje, la matemática funcionó:
            Toast.makeText(context, "Alarma de prueba en 10seg programada", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error al programar prueba: ${e.message}", Toast.LENGTH_LONG).show()
        }
        // -------------------------------------------------------

        val currentTime = System.currentTimeMillis()

        // 2. Programar alertas reales
        TRIGGER_TIMES.forEachIndexed { index, (offsetMillis, prefix) ->
            val triggerAtMillis = eventTimeMillis - offsetMillis

            if (triggerAtMillis > currentTime) {
<<<<<<< Updated upstream

                val intent = Intent(context, ReminderReceiver::class.java).apply {
                    putExtra("title", reminder.title)
                    putExtra("message", "$prefix ${reminder.title}")
                    putExtra("id", reminder.id * 1000 + index)
                }

                val uniqueRequestCode = reminder.id * 1000 + index

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    uniqueRequestCode,
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
=======
                // Usamos hashCode para convertir el ID de texto a número
                val uniqueId = reminder.id.hashCode() + index
                scheduleAlarm(context, alarmManager, triggerAtMillis, reminder, prefix, uniqueId)
>>>>>>> Stashed changes
            }
        }
    }

    // Función auxiliar para programar (Evita repetir código)
    private fun scheduleAlarm(context: Context, alarmManager: AlarmManager, time: Long, reminder: Reminder, prefix: String, id: Int) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", reminder.title)
            putExtra("message", "$prefix ${reminder.title}")
            putExtra("id", id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                // Si no hay permiso exacto, usamos alarma normal
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent)
            } else {
                // Alarma exacta (ideal para recordatorios)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun parseDateToMillis(dateString: String, timeString: String): Long {
        return try {
            // Limpieza agresiva de la cadena de hora para evitar errores con "p. m." vs "PM"
            val cleanDate = dateString.trim()
            var cleanTime = timeString.trim().uppercase() // Forzamos mayúsculas
                .replace("P. M.", "PM")
                .replace("A. M.", "AM")
                .replace("P.M.", "PM")
                .replace("A.M.", "AM")

            val combined = "$cleanDate $cleanTime"

            // Formato que coincida con AddReminderFragment
            // Nota: Usamos Locale.US para la hora (AM/PM) y ES para la fecha (Diciembre)
            // Es un truco para evitar problemas de compatibilidad
            val dateFormat = SimpleDateFormat("dd MMMM, yyyy", Locale("es", "ES"))
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)

            val datePart = dateFormat.parse(cleanDate)
            val timePart = timeFormat.parse(cleanTime)

            if (datePart != null && timePart != null) {
                val calendar = Calendar.getInstance()
                calendar.time = datePart

                val timeCal = Calendar.getInstance()
                timeCal.time = timePart

                calendar.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                calendar.set(Calendar.SECOND, 0)

                return calendar.timeInMillis
            }
            -1L
        } catch (e: Exception) {
            -1L
        }
    }
}