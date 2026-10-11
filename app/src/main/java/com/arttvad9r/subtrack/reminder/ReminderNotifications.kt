package com.arttvad9r.subtrack.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object ReminderNotifications {
    fun createChannel(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            ReminderContract.CHANNEL_ID,
            "Напоминания о подписках",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Предупреждения о продлении и окончании подписок"
        }
        manager.createNotificationChannel(channel)
    }
}
