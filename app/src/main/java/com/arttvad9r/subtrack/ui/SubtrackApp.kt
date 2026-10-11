package com.arttvad9r.subtrack.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SubtrackApp(viewModel: SubtrackViewModel) {
    val subscriptions by viewModel.subscriptions.collectAsStateWithLifecycle()
    var destination by rememberSaveable { mutableStateOf("list") }
    var selectedId by rememberSaveable { mutableLongStateOf(0L) }

    NotificationPermissionEffect(enabled = subscriptions.isNotEmpty())

    when (destination) {
        "list" -> SubscriptionListScreen(
            subscriptions = subscriptions,
            onAdd = {
                selectedId = 0L
                destination = "edit"
            },
            onOpen = { id ->
                selectedId = id
                destination = "detail"
            },
        )

        "detail" -> {
            val subscription = subscriptions.firstOrNull { it.id == selectedId }
            if (subscription == null) {
                LaunchedEffect(selectedId, subscriptions) {
                    if (subscriptions.isNotEmpty()) destination = "list"
                }
            } else {
                SubscriptionDetailScreen(
                    subscription = subscription,
                    onBack = { destination = "list" },
                    onEdit = { destination = "edit" },
                    onDelete = {
                        viewModel.delete(subscription) {
                            destination = "list"
                        }
                    },
                )
            }
        }

        "edit" -> {
            val subscription = subscriptions.firstOrNull { it.id == selectedId }
            SubscriptionEditScreen(
                existing = subscription,
                onBack = {
                    destination = if (selectedId == 0L) "list" else "detail"
                },
                onSave = { draft ->
                    viewModel.save(draft) { id ->
                        selectedId = id
                        destination = "detail"
                    }
                },
            )
        }
    }
}

@Composable
private fun NotificationPermissionEffect(enabled: Boolean) {
    if (!enabled || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    var requested by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    LaunchedEffect(enabled) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted && !requested) {
            requested = true
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
