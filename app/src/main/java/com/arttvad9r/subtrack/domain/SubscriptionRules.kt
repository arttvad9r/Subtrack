package com.arttvad9r.subtrack.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit

fun rollForwardDate(
    date: LocalDate,
    period: BillingPeriod,
    today: LocalDate = LocalDate.now(),
): LocalDate {
    if (period == BillingPeriod.NONE || !date.isBefore(today)) return date

    var result = date
    while (result.isBefore(today)) {
        result = nextPeriodDate(result, period)
    }
    return result
}

fun nextPeriodDate(date: LocalDate, period: BillingPeriod): LocalDate = when (period) {
    BillingPeriod.NONE -> date
    BillingPeriod.MONTHLY -> advanceKeepingEndOfMonth(date, months = 1)
    BillingPeriod.YEARLY -> advanceKeepingEndOfMonth(date, years = 1)
}

private fun advanceKeepingEndOfMonth(
    date: LocalDate,
    months: Long = 0,
    years: Long = 0,
): LocalDate {
    val sourceMonth = YearMonth.from(date)
    val targetMonth = sourceMonth.plusMonths(months).plusYears(years)
    val targetDay = if (date.dayOfMonth == sourceMonth.lengthOfMonth()) {
        targetMonth.lengthOfMonth()
    } else {
        minOf(date.dayOfMonth, targetMonth.lengthOfMonth())
    }
    return targetMonth.atDay(targetDay)
}

fun nextFutureReminderTargetDate(
    currentTargetDate: LocalDate,
    period: BillingPeriod,
    reminderDaysBefore: Int,
    now: LocalDateTime = LocalDateTime.now(),
): LocalDate {
    require(period != BillingPeriod.NONE)
    var target = nextPeriodDate(currentTargetDate, period)
    while (!target.minusDays(reminderDaysBefore.toLong()).atTime(9, 0).isAfter(now)) {
        target = nextPeriodDate(target, period)
    }
    return target
}

fun normalized(subscription: Subscription, today: LocalDate = LocalDate.now()): Subscription {
    if (!subscription.autoRenew || subscription.billingPeriod == BillingPeriod.NONE) return subscription
    return subscription.copy(date = rollForwardDate(subscription.date, subscription.billingPeriod, today))
}

fun reminderDate(subscription: Subscription): LocalDate =
    subscription.date.minusDays(subscription.reminderDaysBefore.toLong())

fun relativeDateText(subscription: Subscription, today: LocalDate = LocalDate.now()): String {
    val days = ChronoUnit.DAYS.between(today, subscription.date)
    return when {
        days < 0 -> "истекла ${-days} дн. назад"
        days == 0L && subscription.autoRenew -> "платёж сегодня"
        days == 0L -> "истекает сегодня"
        days == 1L -> if (subscription.autoRenew) "платёж завтра" else "истекает завтра"
        subscription.autoRenew -> "через $days дн."
        else -> "истекает через $days дн."
    }
}

fun parsePriceMinor(input: String): Long? {
    val normalized = input.trim().replace(',', '.')
    if (normalized.isEmpty()) return null
    return runCatching {
        val value = BigDecimal(normalized)
        if (value.signum() < 0) return null
        value.setScale(2, RoundingMode.UNNECESSARY).movePointRight(2).longValueExact()
    }.getOrNull()
}

fun formatPrice(priceMinor: Long, currency: String): String {
    val amount = BigDecimal.valueOf(priceMinor, 2).stripTrailingZeros().toPlainString()
    val symbol = when (currency.uppercase()) {
        "RUB" -> "₽"
        "USD" -> "$"
        "EUR" -> "€"
        else -> currency.uppercase()
    }
    return "$amount $symbol"
}

fun monthlyEquivalentMinor(subscription: Subscription): Long? {
    if (!subscription.autoRenew) return null
    return when (subscription.billingPeriod) {
        BillingPeriod.NONE -> null
        BillingPeriod.MONTHLY -> subscription.priceMinor
        BillingPeriod.YEARLY -> (subscription.priceMinor + 6L) / 12L
    }
}

fun normalizedUrl(raw: String): String {
    val value = raw.trim()
    if (value.isEmpty()) return value
    return if ("://" in value) value else "https://$value"
}

fun isValidWebUrl(raw: String): Boolean {
    if (raw.isBlank()) return false
    val uri = runCatching { URI(normalizedUrl(raw)) }.getOrNull() ?: return false
    return uri.scheme?.lowercase() in setOf("http", "https") && !uri.host.isNullOrBlank()
}
