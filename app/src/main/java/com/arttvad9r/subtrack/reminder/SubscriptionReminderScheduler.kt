package com.arttvad9r.subtrack.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.arttvad9r.subtrack.domain.Subscription
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class SubscriptionReminderScheduler(context: Context) {
    private val appContext = context.applicationContext
    private val alarmManager = appContext.getSystemService(AlarmManager::class.java)

    fun schedule(subscription: Subscription, catchUpIfPast: Boolean = true) {
        if (subscription.id <= 0L) return
        if (!subscription.autoRenew && subscription.date.isBefore(LocalDate.now())) {
            cancel(subscription.id)
            return
        }

        val desired = subscription.date
            .minusDays(subscription.reminderDaysBefore.toLong())
            .atTime(LocalTime.of(9, 0))
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val now = System.currentTimeMillis()
        if (desired <= now && !catchUpIfPast) return
        val triggerAtMillis = if (desired <= now) now + 2_000L else desired

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent(subscription, PendingIntent.FLAG_UPDATE_CURRENT),
        )
    }

    fun cancel(subscriptionId: Long) {
        if (subscriptionId <= 0L) return
        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            requestCode(subscriptionId),
            Intent(appContext, ReminderReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        ) ?: return
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun pendingIntent(subscription: Subscription, updateFlag: Int): PendingIntent {
        val intent = Intent(appContext, ReminderReceiver::class.java).apply {
            putExtra(ReminderContract.EXTRA_ID, subscription.id)
            putExtra(ReminderContract.EXTRA_NAME, subscription.name)
            putExtra(ReminderContract.EXTRA_PRICE_MINOR, subscription.priceMinor)
            putExtra(ReminderContract.EXTRA_CURRENCY, subscription.currency)
            putExtra(ReminderContract.EXTRA_TARGET_DATE, subscription.date.toString())
            putExtra(ReminderContract.EXTRA_PERIOD, subscription.billingPeriod.name)
            putExtra(ReminderContract.EXTRA_AUTO_RENEW, subscription.autoRenew)
            putExtra(ReminderContract.EXTRA_REMINDER_DAYS, subscription.reminderDaysBefore)
        }
        return PendingIntent.getBroadcast(
            appContext,
            requestCode(subscription.id),
            intent,
            updateFlag or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun requestCode(id: Long): Int = (id xor (id ushr 32)).toInt()
}
