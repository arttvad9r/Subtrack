package com.arttvad9r.subtrack.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
                title = {
                    Text(
                        "Подписки",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
            )
        },
        floatingActionButton = {
            if (subscriptions.isNotEmpty()) {
                FloatingActionButton(
                    onClick = onAdd,
                    modifier = Modifier.testTag(UiTags.ADD),
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Добавить подписку")
                }
            }
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
        modifier = modifier.fillMaxSize().padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Box(
                    modifier = Modifier.size(76.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Rounded.CreditCard,
                        contentDescription = null,
                        modifier = Modifier.size(34.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
            Text(
                "Подписок пока нет",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )
            Text(
                "Добавь первую подписку — дату платежа и напоминание приложение возьмёт на себя.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = onAdd,
                modifier = Modifier.padding(top = 6.dp).testTag(UiTags.ADD),
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
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
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            MonthlySummary(subscriptions = subscriptions)
        }

        items(subscriptions, key = { it.id }) { subscription ->
            SubscriptionCard(
                subscription = subscription,
                onClick = { onOpen(subscription.id) },
            )
        }
    }
}

@Composable
private fun MonthlySummary(subscriptions: List<Subscription>) {
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
    val approximate = subscriptions.any {
        it.autoRenew && it.billingPeriod == BillingPeriod.YEARLY
    }
    val activeCount = subscriptions.count { it.autoRenew }

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                "Регулярные расходы",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f),
            )
            Text(
                (if (approximate) "≈ " else "") + summary + " / мес",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                activeSubscriptionsText(activeCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f),
            )
        }
    }
}

@Composable
private fun SubscriptionCard(subscription: Subscription, onClick: () -> Unit) {
    val expired = !subscription.autoRenew && subscription.date.isBefore(LocalDate.now())
    val formatter = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("ru-RU"))

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(46.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        subscription.name.firstOrNull()?.uppercase() ?: "•",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    subscription.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${relativeDateText(subscription)} · ${subscription.date.format(formatter)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (expired) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.width(12.dp))

            Text(
                priceWithPeriod(subscription),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
    }
}

private fun priceWithPeriod(subscription: Subscription): String {
    val suffix = when (subscription.billingPeriod) {
        BillingPeriod.NONE -> ""
        BillingPeriod.MONTHLY -> " / мес"
        BillingPeriod.YEARLY -> " / год"
    }
    return formatPrice(subscription.priceMinor, subscription.currency) + suffix
}

private fun activeSubscriptionsText(count: Int): String = when {
    count % 10 == 1 && count % 100 != 11 -> "$count активная подписка"
    count % 10 in 2..4 && count % 100 !in 12..14 -> "$count активные подписки"
    else -> "$count активных подписок"
}
