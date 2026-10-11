package com.arttvad9r.subtrack.debug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.arttvad9r.subtrack.domain.BillingPeriod
import com.arttvad9r.subtrack.reminder.ReminderContract
import com.arttvad9r.subtrack.reminder.ReminderReceiver
import java.time.LocalDate

class DebugReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_TEST_REMINDER) return
        val targetDate = LocalDate.now().plusDays(3)
        val reminderIntent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderContract.EXTRA_ID, DEBUG_ID)
            putExtra(ReminderContract.EXTRA_NAME, "Subtrack test notification")
            putExtra(ReminderContract.EXTRA_PRICE_MINOR, 1_999L)
            putExtra(ReminderContract.EXTRA_CURRENCY, "USD")
            putExtra(ReminderContract.EXTRA_TARGET_DATE, targetDate.toString())
            putExtra(ReminderContract.EXTRA_PERIOD, BillingPeriod.NONE.name)
            putExtra(ReminderContract.EXTRA_AUTO_RENEW, false)
            putExtra(ReminderContract.EXTRA_REMINDER_DAYS, 3)
        }
        ReminderReceiver().onReceive(context, reminderIntent)
    }

    companion object {
        const val ACTION_TEST_REMINDER = "com.arttvad9r.subtrack.DEBUG_TEST_REMINDER"
        private const val DEBUG_ID = 9_999_999L
    }
}
