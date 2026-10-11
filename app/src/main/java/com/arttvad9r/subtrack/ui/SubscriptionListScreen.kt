package com.arttvad9r.subtrack.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arttvad9r.subtrack.domain.BillingPeriod
import com.arttvad9r.subtrack.domain.Subscription
import com.arttvad9r.subtrack.domain.formatPrice
import com.arttvad9r.subtrack.domain.monthlyEquivalentMinor
import com.arttvad9r.subtrack.domain.relativeDateText
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionListScreen(
    subscriptions: List<Subscription>,
    onAdd: () -> Unit,
    onOpen: (Long) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Подписки") },
                actions = {
                    IconButton(onClick = onAdd, modifier = Modifier.testTag(UiTags.ADD)) {
                        Icon(Icons.Rounded.Add, contentDescription = "Добавить подписку")
                    }
                },
            )
        },
    ) { padding ->
        if (subscriptions.isEmpty()) {
            EmptySubscriptions(
                modifier = Modifier.padding(padding),
                onAdd = onAdd,
            )
        } else {
            SubscriptionList(
                subscriptions = subscriptions,
                modifier = Modifier.padding(padding),
                onOpen = onOpen,
            )
        }
    }
}

@Composable
private fun EmptySubscriptions(modifier: Modifier = Modifier, onAdd: () -> Unit) {
    Box(
        modifier = modifier.fillMaxSize().padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Подписок пока нет", style = MaterialTheme.typography.titleMedium)
            Text(
                "Добавьте первую подписку, чтобы не пропустить оплату или окончание.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Button(onClick = onAdd, modifier = Modifier.testTag(UiTags.ADD)) {
                Text("Добавить подписку")
            }
        }
    }
}

@Composable
private fun SubscriptionList(
    subscriptions: List<Subscription>,
    modifier: Modifier = Modifier,
    onOpen: (Long) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            MonthlySummary(
                subscriptions = subscriptions,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }

        items(subscriptions, key = { it.id }) { subscription ->
            SubscriptionRow(subscription = subscription, onClick = { onOpen(subscription.id) })
            HorizontalDivider(modifier = Modifier.padding(start = 16.dp))
        }
    }
}

@Composable
private fun MonthlySummary(subscriptions: List<Subscription>, modifier: Modifier = Modifier) {
    val totals = subscriptions
        .mapNotNull { subscription ->
            monthlyEquivalentMinor(subscription)?.let { subscription.currency to it }
        }
        .groupBy({ it.first }, { it.second })
        .mapValues { (_, values) -> values.sum() }

    if (totals.isEmpty()) return

    val summary = totals.entries.joinToString(" + ") { (currency, amount) ->
        formatPrice(amount, currency)
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            "В месяц",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            "≈ $summary",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun SubscriptionRow(subscription: Subscription, onClick: () -> Unit) {
    val expired = !subscription.autoRenew && subscription.date.isBefore(LocalDate.now())
    val formatter = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("ru-RU"))

    ListItem(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        headlineContent = {
            Text(
                subscription.name,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(
                "${relativeDateText(subscription)} · ${subscription.date.format(formatter)}",
                color = if (expired) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        trailingContent = {
            Text(
                priceWithPeriod(subscription),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )
        },
    )
}

private fun priceWithPeriod(subscription: Subscription): String {
    val suffix = when (subscription.billingPeriod) {
        BillingPeriod.NONE -> ""
        BillingPeriod.MONTHLY -> " / мес"
        BillingPeriod.YEARLY -> " / год"
    }
    return formatPrice(subscription.priceMinor, subscription.currency) + suffix
}
