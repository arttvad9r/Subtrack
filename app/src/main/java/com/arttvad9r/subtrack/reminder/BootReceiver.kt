package com.arttvad9r.subtrack.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.arttvad9r.subtrack.SubtrackApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val app = context.applicationContext as SubtrackApplication
                app.container.repository.reconcileAndScheduleAll()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
