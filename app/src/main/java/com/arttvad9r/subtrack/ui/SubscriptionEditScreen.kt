package com.arttvad9r.subtrack.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arttvad9r.subtrack.domain.BillingPeriod
import com.arttvad9r.subtrack.domain.Subscription
import com.arttvad9r.subtrack.domain.parsePriceMinor
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionEditScreen(
    existing: Subscription?,
    onBack: () -> Unit,
    onSave: (Subscription) -> Unit,
) {
    val key = existing?.id ?: 0L
    var name by rememberSaveable(key) { mutableStateOf(existing?.name.orEmpty()) }
    var price by rememberSaveable(key) { mutableStateOf(existing?.let { priceInput(it.priceMinor) }.orEmpty()) }
    var currency by rememberSaveable(key) { mutableStateOf(existing?.currency ?: "RUB") }
    var dateIso by rememberSaveable(key) { mutableStateOf((existing?.date ?: LocalDate.now().plusMonths(1)).toString()) }
    var periodName by rememberSaveable(key) { mutableStateOf((existing?.billingPeriod ?: BillingPeriod.MONTHLY).name) }
    var autoRenew by rememberSaveable(key) { mutableStateOf(existing?.autoRenew ?: true) }
    var reminder by rememberSaveable(key) { mutableStateOf((existing?.reminderDaysBefore ?: 3).toString()) }
    var note by rememberSaveable(key) { mutableStateOf(existing?.note.orEmpty()) }
    var url by rememberSaveable(key) { mutableStateOf(existing?.url.orEmpty()) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    val selectedDate = remember(dateIso) { LocalDate.parse(dateIso) }
    val period = BillingPeriod.valueOf(periodName)
    val priceMinor = parsePriceMinor(price)
    val reminderDays = reminder.toIntOrNull()
    val valid = name.isNotBlank() &&
        priceMinor != null &&
        currency.trim().length == 3 &&
        currency.trim().all { it.isLetter() } &&
        reminderDays != null && reminderDays in 0..365

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existing == null) "Новая подписка" else "Изменить") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    TextButton(
                        enabled = valid,
                        onClick = {
                            onSave(
                                Subscription(
                                    id = existing?.id ?: 0L,
                                    name = name,
                                    priceMinor = requireNotNull(priceMinor),
                                    currency = currency.trim().uppercase(),
                                    date = selectedDate,
                                    billingPeriod = period,
                                    autoRenew = autoRenew && period != BillingPeriod.NONE,
                                    reminderDaysBefore = requireNotNull(reminderDays),
                                    note = note,
                                    url = url,
                               ),
                            )
                        },
                    ) {
                        Text("Сохранить")
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Цена") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    isError = price.isNotBlank() && priceMinor == null,
                )
                OutlinedTextField(
                    value = currency,
                    onValueChange = { currency = it.take(3).uppercase() },
                    label = { Text("Валюта") },
                    singleLine = true,
                    modifier = Modifier.weight(0.65f),
                )
            }

            OutlinedTextField(
                value = selectedDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())),
                onValueChange = { },
                readOnly = true,
                label = { Text(if (autoRenew) "Следующий платёд" else "Окончание") },
                trailingIcon = { Icon(Icons.Rounded.DateRange, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
            )

            Text("Повторение", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PeriodChip("Нет", BillingPeriod.NONE, period) {
                    periodName = it.name
                    autoRenew = false
                }
                PeriodChip("Месяц", BillingPeriod.MONTHLY, period) { periodName = it.name }
                PeriodChip("Год", BillingPeriod.YEARLY, period) { periodName = it.name }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Автопродление", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Дата считается следующим платежом",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = autoRenew,
                    enabled = period != BillingPeriod.NONE,
                    onCheckedChange = { autoRenew = it },
                )
            }

            OutlinedTextField(
                value = reminder,
                onValueChange = { reminder = it.filter(Char::isDigit).take(3) },
                label = { Text("Напомнить за, дней") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = reminder.isNotBlank() && reminderDays !in 0..365,
            )

            Text("Дополнительно", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Комментарий") },
                placeholder = { Text("Например, где куплена подписка") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Сайт") },
                placeholder = { Text("example.com") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            dateIso = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                                .toString()
                        }
                        showDatePicker = false
                    },
                ) { Text("Готово") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun PeriodChip(
    text: String,
    value: BillingPeriod,
    selected: BillingPeriod,
    onSelect: (BillingPeriod) -> Unit,
) {
    FilterChip(
        selected = selected == value,
        onClick = { onSelect(value) },
        label = { Text(text) },
    )
}

private fun priceInput(priceMinor: Long): String =
    BigDecimal.valueOf(priceMinor, 2).stripTrailingZeros().toPlainString()
