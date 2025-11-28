package com.example.organizer11.utils

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.organizer11.data.model.Reminder
import com.example.organizer11.ui.worker.ReminderWorker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    fun scheduleNotifications(context: Context, reminder: Reminder) {
        // 1. Convertir los Strings de fecha/hora a un Timestamp real (milisegundos)
        val dueTimestamp = parseDateToMillis(reminder.endDate, reminder.dueTime) ?: return
        val currentTimestamp = System.currentTimeMillis()

        // 2. Definir los tiempos de antelación (en milisegundos)
        val timesBefore = listOf(
            Triple(3 * 24 * 60 * 60 * 1000L, "3 días", 1), // 3 días
            Triple(1 * 24 * 60 * 60 * 1000L, "1 día", 2),  // 1 día
            Triple(12 * 60 * 60 * 1000L, "12 horas", 3),   // 12 horas
            Triple(1 * 60 * 60 * 1000L, "1 hora", 4)       // 1 hora
        )

        // 3. Programar cada una
        for ((offset, label, uniqueIdSuffix) in timesBefore) {
            val triggerTime = dueTimestamp - offset

            // Solo programar si la fecha es en el futuro
            if (triggerTime > currentTimestamp) {
                val delay = triggerTime - currentTimestamp

                scheduleWorker(
                    context,
                    delay,
                    "Recordatorio: ${reminder.title}",
                    "Vence en $label. ¡No lo olvides!",
                    reminder.id + uniqueIdSuffix // ID único para cada notificación
                )
            }
        }
    }

    private fun scheduleWorker(context: Context, delay: Long, title: String, msg: String, notifId: Int) {
        val data = Data.Builder()
            .putString("title", title)
            .putString("message", msg)
            .putInt("id", notifId)
            .build()

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    // Función auxiliar para convertir "05 May, 2025" + "12:00 PM" a Long
    fun parseDateToMillis(dateStr: String, timeStr: String): Long? {
        return try {
            val fullFormat = SimpleDateFormat("dd MMMM, yyyy hh:mm a", Locale("es", "ES"))
            val combinedStr = "$dateStr $timeStr"
            fullFormat.parse(combinedStr)?.time
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}