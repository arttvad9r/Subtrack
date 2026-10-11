package com.arttvad9r.subtrack

import android.app.Application
import com.arttvad9r.subtrack.reminder.ReminderNotifications

class SubtrackApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }

    override fun onCreate() {
        super.onCreate()
        ReminderNotifications.createChannel(this)
    }
}
