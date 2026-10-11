package com.arttvad9r.subtrack.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arttvad9r.subtrack.domain.BillingPeriod
import com.arttvad9r.subtrack.domain.Subscription
import com.arttvad9r.subtrack.domain.isValidWebUrl
import com.arttvad9r.subtrack.domain.parsePriceMinor
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ReminderPresets = listOf(0, 1, 3, 7)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    var customReminder by rememberSaveable(key) {
        mutableStateOf(existing?.reminderDaysBefore?.let { it !in ReminderPresets } ?: false)
    }
    var note by rememberSaveable(key) { mutableStateOf(existing?.note.orEmpty()) }
    var url by rememberSaveable(key) { mutableStateOf(existing?.url.orEmpty()) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showAdvanced by rememberSaveable(key) {
        mutableStateOf(existing?.let { it.note.isNotBlank() || it.url.isNotBlank() } ?: false)
    }

    val selectedDate = remember(dateIso) { LocalDate.parse(dateIso) }
    val period = BillingPeriod.valueOf(periodName)
    val priceMinor = parsePriceMinor(price)
    val reminderDays = reminder.toIntOrNull()
    val currencyValid = currency.trim().length == 3 && currency.trim().all(Char::isLetter)
    val urlValid = url.isBlank() || isValidWebUrl(url)
    val valid = name.isNotBlank() &&
        priceMinor != null &&
        currencyValid &&
        reminderDays != null && reminderDays in 0..365 &&
        urlValid

    fun save() {
        onSave(
            Subscription(
                id = existing?.id ?: 0L,
                name = name.trim(),
                priceMinor = requireNotNull(priceMinor),
                currency = currency.trim().uppercase(),
                date = selectedDate,
                billingPeriod = period,
                autoRenew = autoRenew && period != BillingPeriod.NONE,
                reminderDaysBefore = requireNotNull(reminderDays),
                note = note.trim(),
                url = url.trim(),
            ),
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (existing == null) "Новая подписка" else "Изменить подписку",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    TextButton(
                        onClick = ::save,
                        enabled = valid,
                        modifier = Modifier.testTag(UiTags.SAVE),
                    ) {
                        Text("Сохранить", fontWeight = FontWeight.Medium)
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
                "Основное",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = Space.s, bottom = Space.m),
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название") },
                placeholder = { Text("Например: ChatGPT Plus") },
                singleLine = true,
                shape = Radius.field,
                modifier = Modifier.fillMaxWidth().testTag(UiTags.NAME),
            )
            Row(
                modifier = Modifier.padding(top = Space.s),
                horizontalArrangement = Arrangement.spacedBy(Space.s),
            ) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Цена") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = Radius.field,
                    isError = price.isNotBlank() && priceMinor == null,
                    modifier = Modifier.weight(1f).testTag(UiTags.PRICE),
                )
                OutlinedTextField(
                    value = currency,
                    onValueChange = { currency = it.filter(Char::isLetter).take(3).uppercase() },
                    label = { Text("Валюта") },
                    singleLine = true,
                    shape = Radius.field,
                    isError = currency.isNotBlank() && !currencyValid,
                    modifier = Modifier.weight(0.62f).testTag(UiTags.CURRENCY),
                )
            }
            if (price.isNotBlank() && priceMinor == null) {
                Text(
                    "Цена: не больше двух знаков после запятой",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = Space.xs),
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = Space.l),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            Text("Оплата", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
                    .heightIn(min = 56.dp)
                    .testTag(UiTags.DATE),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (autoRenew) "Следующий платёж" else "Окончание",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        selectedDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru-RU"))),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    "Изменить",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Icon(
                    Icons.Rounded.CalendarMonth,
                    contentDescription = "Выбрать дату",
                    modifier = Modifier.padding(start = Space.s).size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Text(
                "Повторение",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = Space.m),
            )
            FlowRow(
                modifier = Modifier.padding(top = Space.s),
                horizontalArrangement = Arrangement.spacedBy(Space.s),
                verticalArrangement = Arrangement.spacedBy(Space.xs),
            ) {
                PeriodChip("Нет", BillingPeriod.NONE, period) {
                    periodName = it.name
                    autoRenew = false
                }
                PeriodChip("Месяц", BillingPeriod.MONTHLY, period) {
                    periodName = it.name
                    autoRenew = true
                }
                PeriodChip("Год", BillingPeriod.YEARLY, period) {
                    periodName = it.name
                    autoRenew = true
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Space.m),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Автопродление", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        if (autoRenew) "Дата считается следующим платежом" else "Дата считается окончанием",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.width(Space.s))
                Switch(
                    checked = autoRenew,
                    enabled = period != BillingPeriod.NONE,
                    onCheckedChange = { autoRenew = it },
                    colors = subtrackSwitchColors(),
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = Space.l),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            Text("Напоминание", style = MaterialTheme.typography.titleMedium)
            Text(
                "Когда предупредить до платежа или окончания",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Space.xs),
            )
            FlowRow(
                modifier = Modifier.padding(top = Space.s),
                horizontalArrangement = Arrangement.spacedBy(Space.s),
                verticalArrangement = Arrangement.spacedBy(Space.xs),
            ) {
                ReminderChip("В день", 0, reminderDays, customReminder) {
                    reminder = "0"; customReminder = false
                }
                ReminderChip("1 день", 1, reminderDays, customReminder) {
                    reminder = "1"; customReminder = false
                }
                ReminderChip("3 дня", 3, reminderDays, customReminder) {
                    reminder = "3"; customReminder = false
                }
                ReminderChip("7 дней", 7, reminderDays, customReminder) {
                    reminder = "7"; customReminder = false
                }
            }
            TextButton(
                onClick = { customReminder = true },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = Space.s),
            ) {
                Text(if (customReminder) "Своё значение" else "Другое значение")
            }
            if (customReminder) {
                OutlinedTextField(
                    value = reminder,
                    onValueChange = { reminder = it.filter(Char::isDigit).take(3) },
                    label = { Text("Дней до напоминания") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = Radius.field,
                    isError = reminder.isNotBlank() && reminderDays !in 0..365,
                    supportingText = if (reminder.isNotBlank() && reminderDays !in 0..365) {
                        { Text("Введите от 0 до 365 дней") }
                    } else null,
                    modifier = Modifier.fillMaxWidth().testTag(UiTags.REMINDER),
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = Space.l),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAdvanced = !showAdvanced }
                    .heightIn(min = 56.dp)
                    .testTag(UiTags.ADVANCED),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Дополнительно", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Комментарий и ссылка на сайт",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Icon(
                    if (showAdvanced) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (showAdvanced) {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Комментарий") },
                    placeholder = { Text("Где куплена подписка") },
                    minLines = 2,
                    maxLines = 4,
                    shape = Radius.field,
                    modifier = Modifier.fillMaxWidth().padding(top = Space.s).testTag(UiTags.NOTE),
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Сайт") },
                    placeholder = { Text("example.com") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    singleLine = true,
                    shape = Radius.field,
                    isError = url.isNotBlank() && !urlValid,
                    supportingText = if (url.isNotBlank() && !urlValid) {
                        { Text("Введите адрес сайта, например example.com") }
                    } else null,
                    modifier = Modifier.fillMaxWidth().padding(top = Space.s).testTag(UiTags.URL),
                )
            }
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
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        dateIso = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .toString()
                    }
                    showDatePicker = false
                }) { Text("Готово") }
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
        shape = Radius.field,
        colors = selectionChipColors(),
    )
}

@Composable
private fun ReminderChip(
    text: String,
    value: Int,
    selected: Int?,
    custom: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = !custom && selected == value,
        onClick = onClick,
        label = { Text(text) },
        shape = Radius.field,
        colors = selectionChipColors(),
    )
}

private fun priceInput(priceMinor: Long): String =
    BigDecimal.valueOf(priceMinor, 2).stripTrailingZeros().toPlainString()
