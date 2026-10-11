package com.arttvad9r.subtrack.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
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
        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru-RU"))
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Подписка", style = MaterialTheme.typography.titleLarge) },
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Space.screen)
                .padding(bottom = 32.dp),
        ) {
            Text(
                subscription.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = Space.s),
            )
            Text(
                formatPrice(subscription.priceMinor, subscription.currency),
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(top = Space.xs, bottom = Space.m),
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = Radius.card,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Row(
                    modifier = Modifier.padding(Space.m),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            if (subscription.autoRenew) "Следующий платёж" else "Окончание",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Text(
                            subscription.date.format(dateFormatter),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(top = Space.xs),
                        )
                    }
                    Text(
                        periodShort(subscription.billingPeriod),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth().padding(top = Space.l),
                shape = Radius.card,
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column {
                    DetailRow(
                        icon = Icons.Rounded.Repeat,
                        label = "Период",
                        value = periodText(subscription.billingPeriod),
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = Space.m),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                    DetailRow(
                        icon = Icons.Rounded.Autorenew,
                        label = "Автопродление",
                        value = if (subscription.autoRenew) "Включено" else "Выключено",
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = Space.m),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                    DetailRow(
                        icon = Icons.Rounded.Notifications,
                        label = "Напоминание",
                        value = if (subscription.reminderDaysBefore == 0) {
                            "В день события"
                        } else {
                            "За ${subscription.reminderDaysBefore} дн."
                        },
                    )
                }
            }

            if (subscription.note.isNotBlank()) {
                InfoCard(
                    icon = Icons.AutoMirrored.Rounded.Notes,
                    title = "Комментарий",
                    value = subscription.note,
                    modifier = Modifier.padding(top = Space.s),
                )
            }

            if (subscription.url.isNotBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(top = Space.s),
                    shape = Radius.card,
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(Space.m),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Rounded.Language,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.width(Space.m))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Сайт",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                subscription.url,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        TextButton(onClick = {
                            runCatching { uriHandler.openUri(normalizedUrl(subscription.url)) }
                        }) {
                            Text("Открыть")
                        }
                    }
                }
            }

            TextButton(
                onClick = { confirmDelete = true },
                modifier = Modifier.padding(top = Space.m).testTag(UiTags.DELETE),
            ) {
                Text("Удалить подписку", color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            shape = Radius.card,
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Удалить подписку?", style = MaterialTheme.typography.titleLarge) },
            text = { Text("«${subscription.name}» будет удалена вместе с напоминанием.") },
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
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(Space.m),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(Space.m))
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun InfoCard(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = Radius.card,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Space.m),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.width(Space.m))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    value,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = Space.xs),
                )
            }
        }
    }
}

private fun periodText(period: BillingPeriod): String = when (period) {
    BillingPeriod.NONE -> "Без повторения"
    BillingPeriod.MONTHLY -> "Каждый месяц"
    BillingPeriod.YEARLY -> "Каждый год"
}

private fun periodShort(period: BillingPeriod): String = when (period) {
    BillingPeriod.NONE -> "разово"
    BillingPeriod.MONTHLY -> "каждый месяц"
    BillingPeriod.YEARLY -> "каждый год"
}
