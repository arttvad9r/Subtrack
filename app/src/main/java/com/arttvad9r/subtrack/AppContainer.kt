package com.arttvad9r.subtrack

import android.content.Context
import androidx.room.Room
import com.arttvad9r.subtrack.data.SubtrackDatabase
import com.arttvad9r.subtrack.data.SubscriptionRepository
import com.arttvad9r.subtrack.reminder.SubscriptionReminderScheduler

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        SubtrackDatabase::class.java,
        "subtrack.db",
    ).build()

    private val reminderScheduler = SubscriptionReminderScheduler(context)

    val repository = SubscriptionRepository(
        dao = database.subscriptionDao(),
        reminderScheduler = reminderScheduler,
    )
}
