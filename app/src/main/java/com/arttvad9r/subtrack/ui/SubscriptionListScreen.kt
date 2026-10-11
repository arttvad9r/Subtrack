package com.arttvad9r.subtrack.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Подписки", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        bottomBar = {
            if (subscriptions.isNotEmpty()) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Box(
                        Modifier
                            .navigationBarsPadding()
                            .padding(horizontal = Space.screen, vertical = Space.m),
                    ) {
                        Button(
                            onClick = onAdd,
                            shape = Radius.field,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .testTag(UiTags.ADD),
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(Space.s))
                            Text("Добавить подписку")
                        }
                    }
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
        modifier = modifier.fillMaxSize().padding(horizontal = Space.screen),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(
                    Icons.Outlined.CreditCard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(20.dp).size(40.dp),
                )
            }
            Text(
                "Подписок пока нет",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 24.dp),
            )
            Text(
                "Добавьте сервисы, за которые важно не забыть заплатить или отменить продление.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Space.m),
            )
            Button(
                onClick = onAdd,
                shape = Radius.field,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .heightIn(min = 52.dp)
                    .testTag(UiTags.ADD),
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(Space.s))
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
        contentPadding = PaddingValues(
            start = Space.screen,
            end = Space.screen,
            top = Space.xs,
            bottom = Space.l,
        ),
        verticalArrangement = Arrangement.spacedBy(Space.s),
    ) {
        item("summary") {
            MonthlySummary(subscriptions)
            Spacer(Modifier.size(Space.xs))
        }
        items(subscriptions, key = { it.id }) { subscription ->
            SubscriptionCard(subscription) { onOpen(subscription.id) }
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

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = Radius.card,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(Modifier.padding(Space.m)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Регулярные расходы",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    activeSubscriptionsText(activeCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Text(
                (if (approximate) "≈ " else "") + summary + " / мес",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = Space.s),
            )
        }
    }
}

@Composable
private fun SubscriptionCard(subscription: Subscription, onClick: () -> Unit) {
    val expired = !subscription.autoRenew && subscription.date.isBefore(LocalDate.now())
    val formatter = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("ru-RU"))

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = Radius.card,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Space.m, vertical = Space.s),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(44.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        subscription.name.firstOrNull()?.uppercase() ?: "•",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(Modifier.width(Space.m))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    subscription.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${relativeDateText(subscription)} · ${subscription.date.format(formatter)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (expired) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Spacer(Modifier.width(Space.s))
            Text(
                priceWithPeriod(subscription),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
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
    count % 10 == 1 && count % 100 != 11 -> "$count активная"
    count % 10 in 2..4 && count % 100 !in 12..14 -> "$count активные"
    else -> "$count активных"
}
