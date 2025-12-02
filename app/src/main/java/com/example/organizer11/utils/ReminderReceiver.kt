package com.example.organizer11.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.organizer11.MainActivity
import com.example.organizer11.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Recibir los datos que enviamos desde el Scheduler
        val title = intent.getStringExtra("title") ?: "Recordatorio"
        val message = intent.getStringExtra("message") ?: "Tienes un evento pendiente"
        val reminderId = intent.getIntExtra("id", 0)

        // Mostrar la notificación
        showNotification(context, title, message, reminderId)
    }

    private fun showNotification(context: Context, title: String, message: String, reminderId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "reminder_channel"

        // 1. Crear el canal de notificación (Obligatorio para Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorios",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de tus eventos agendados"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Definir qué pasa al tocar la notificación (Abrir la app)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 3. Construir el diseño de la notificación
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round) // Usamos el icono de tu app
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Se borra al tocarla
            .build()

        // 4. Lanzar la notificación
        notificationManager.notify(reminderId, notification)
    }
}