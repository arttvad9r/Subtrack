package com.arttvad9r.subtrack.reminder

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.arttvad9r.subtrack.MainActivity
import com.arttvad9r.subtrack.R
import com.arttvad9r.subtrack.domain.BillingPeriod
import com.arttvad9r.subtrack.domain.Subscription
import com.arttvad9r.subtrack.domain.formatPrice
import com.arttvad9r.subtrack.domain.nextFutureReminderTargetDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val subscription = intent.toSubscription() ?: return
        ReminderNotifications.createChannel(context)
        showNotification(context, subscription)

        if (subscription.autoRenew && subscription.billingPeriod != BillingPeriod.NONE) {
            SubscriptionReminderScheduler(context).schedule(
                subscription.copy(
                    date = nextFutureReminderTargetDate(
                        currentTargetDate = subscription.date,
                        period = subscription.billingPeriod,
                        reminderDaysBefore = subscription.reminderDaysBefore,
                    ),
                ),
            )
        }
    }

    private fun showNotification(context: Context, subscription: Subscription) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val openApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val date = subscription.date.format(
            DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru-RU")),
        )
        val action = if (subscription.autoRenew) "платёж" else "окончание"
        val text = "${formatPrice(subscription.priceMinor, subscription.currency)} · $action $date"

        val notification = NotificationCompat.Builder(context, ReminderContract.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(subscription.name)
            .setContentText(text)
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context)
            .notify(notificationId(subscription.id), notification)
    }

    private fun Intent.toSubscription(): Subscription? {
        val id = getLongExtra(ReminderContract.EXTRA_ID, 0L)
        val name = getStringExtra(ReminderContract.EXTRA_NAME) ?: return null
        val targetDate = getStringExtra(ReminderContract.EXTRA_TARGET_DATE) ?: return null
        val period = runCatching {
            BillingPeriod.valueOf(getStringExtra(ReminderContract.EXTRA_PERIOD).orEmpty())
        }.getOrNull() ?: return null

        return Subscription(
            id = id,
            name = name,
            priceMinor = getLongExtra(ReminderContract.EXTRA_PRICE_MINOR, 0L),
            currency = getStringExtra(ReminderContract.EXTRA_CURRENCY).orEmpty(),
            date = runCatching { LocalDate.parse(targetDate) }.getOrNull() ?: return null,
            billingPeriod = period,
            autoRenew = getBooleanExtra(ReminderContract.EXTRA_AUTO_RENEW, false),
            reminderDaysBefore = getIntExtra(ReminderContract.EXTRA_REMINDER_DAYS, 3),
        )
    }

    private fun notificationId(id: Long): Int = (id xor (id ushr 32)).toInt()
}
