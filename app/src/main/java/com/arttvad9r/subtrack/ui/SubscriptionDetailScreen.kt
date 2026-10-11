package com.arttvad9r.subtrack.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arttvad9r.subtrack.domain.BillingPeriod
import com.arttvad9r.subtrack.domain.Subscription
import com.arttvad9r.subtrack.domain.formatPrice
import com.arttvad9r.subtrack.domain.normalizedUrl
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailScreen(
    subscription: Subscription,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    var confirmDelete by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(subscription.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Изменить")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                formatPrice(subscription.priceMinor, subscription.currency),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.SemiBold,
            )

            DetailField(
                label = if (subscription.autoRenew) "Следующий платёд" else "Окончание",
                value = subscription.date.format(dateFormatter),
            )
            DetailField("Период", periodText(subscription.billingPeriod))
            DetailField("Автопродление", if (subscription.autoRenew) "Включено" else "Выключено")
            DetailField(
                "Напоминание",
                if (subscription.reminderDaysBefore == 0) "В день события"
                else "За ${subscription.reminderDaysBefore} дн.",
            )

            if (subscription.note.isNotBlank()) {
                HorizontalDivider()
                DetailField("Комментарий", subscription.note)
            }

            if (subscription.url.isNotBlank()) {
                DetailField("Сайт", subscription.url)
                TextButton(
                    onClick = {
                        runCatching { uriHandler.openUri(normalizedUrl(subscription.url)) }
                    },
                ) {
                    Text("Открыть сайт")
                }
            }

            HorizontalDivider()
            TextButton(onClick = { confirmDelete = true }) {
                Text("Удалить подписку", color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Удалить подписку?") },
            text = { Text("${subscription.name} будет удалена вместе с напоминанием.") },
            confirmButton = {
                TextButton(onClick = onDelete) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text("Отмена")
                }
            },
        )
    }
}

@Composable
private fun DetailField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

private fun periodText(period: BillingPeriod): String = when (period) {
    BillingPeriod.NONE -> "Без повторения"
    BillingPeriod.MONTHLY -> "Каждый месяц"
    BillingPeriod.YEARLY -> "Каждый год"
}
