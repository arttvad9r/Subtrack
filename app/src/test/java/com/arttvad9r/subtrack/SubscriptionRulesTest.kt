package com.arttvad9r.subtrack

import com.arttvad9r.subtrack.domain.BillingPeriod
import com.arttvad9r.subtrack.domain.Subscription
import com.arttvad9r.subtrack.domain.isValidWebUrl
import com.arttvad9r.subtrack.domain.monthlyEquivalentMinor
import com.arttvad9r.subtrack.domain.nextFutureReminderTargetDate
import com.arttvad9r.subtrack.domain.nextPeriodDate
import com.arttvad9r.subtrack.domain.normalized
import com.arttvad9r.subtrack.domain.parsePriceMinor
import com.arttvad9r.subtrack.domain.reminderDate
import com.arttvad9r.subtrack.domain.rollForwardDate
import java.time.LocalDate
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SubscriptionRulesTest {
    @Test
    fun `price parser stores exact minor units`() {
        assertEquals(2_050L, parsePriceMinor("20.50"))
        assertEquals(2_050L, parsePriceMinor("20,5"))
        assertEquals(2_000L, parsePriceMinor("20"))
        assertNull(parsePriceMinor("20.555"))
        assertNull(parsePriceMinor("-1"))
    }

    @Test
    fun `monthly date rolls forward to first non-past occurrence`() {
        val result = rollForwardDate(
            date = LocalDate.of(2026, 7, 15),
            period = BillingPeriod.MONTHLY,
            today = LocalDate.of(2026, 10, 11),
        )
        assertEquals(LocalDate.of(2026, 10, 15), result)
    }

    @Test
    fun `month end billing stays at month end`() {
        val february = nextPeriodDate(LocalDate.of(2027, 1, 31), BillingPeriod.MONTHLY)
        val march = nextPeriodDate(february, BillingPeriod.MONTHLY)
        assertEquals(LocalDate.of(2027, 2, 28), february)
        assertEquals(LocalDate.of(2027, 3, 31), march)
    }

    @Test
    fun `leap day yearly billing returns to leap day`() {
        val nonLeap = nextPeriodDate(LocalDate.of(2024, 2, 29), BillingPeriod.YEARLY)
        val leapAgain = rollForwardDate(
            date = nonLeap,
            period = BillingPeriod.YEARLY,
            today = LocalDate.of(2028, 2, 29),
        )
        assertEquals(LocalDate.of(2025, 2, 28), nonLeap)
        assertEquals(LocalDate.of(2028, 2, 29), leapAgain)
    }

    @Test
    fun `non renewing subscription does not roll forward`() {
        val subscription = sample(
            date = LocalDate.of(2026, 9, 1),
            autoRenew = false,
            period = BillingPeriod.YEARLY,
        )
        assertEquals(
            LocalDate.of(2026, 9, 1),
            normalized(subscription, LocalDate.of(2026, 10, 11)).date,
        )
    }

    @Test
    fun `reminder date subtracts lead days`() {
        val subscription = sample(
            date = LocalDate.of(2026, 10, 15),
            reminderDays = 3,
        )
        assertEquals(LocalDate.of(2026, 10, 12), reminderDate(subscription))
    }

    @Test
    fun `recurring reminder skips cycles whose reminder time is already past`() {
        val target = nextFutureReminderTargetDate(
            currentTargetDate = LocalDate.of(2026, 10, 15),
            period = BillingPeriod.MONTHLY,
            reminderDaysBefore = 365,
            now = LocalDateTime.of(2026, 10, 11, 10, 0),
        )
        assertEquals(LocalDate.of(2027, 10, 15), target)
    }

    @Test
    fun `yearly price is converted to approximate monthly minor units`() {
        val subscription = sample(priceMinor = 4_800_00L, period = BillingPeriod.YEARLY)
        assertEquals(40_000L, monthlyEquivalentMinor(subscription))
    }

    @Test
    fun `web url accepts host with or without scheme`() {
        assertTrue(isValidWebUrl("example.com"))
        assertTrue(isValidWebUrl("https://example.com/account"))
        assertFalse(isValidWebUrl("not a url"))
        assertFalse(isValidWebUrl("ftp://example.com"))
        assertFalse(isValidWebUrl(""))
    }

    private fun sample(
        priceMinor: Long = 2_000L,
        date: LocalDate = LocalDate.of(2026, 10, 15),
        autoRenew: Boolean = true,
        period: BillingPeriod = BillingPeriod.MONTHLY,
        reminderDays: Int = 3,
    ) = Subscription(
        id = 1,
        name = "Test",
        priceMinor = priceMinor,
        currency = "RUB",
        date = date,
        billingPeriod = period,
        autoRenew = autoRenew,
        reminderDaysBefore = reminderDays,
    )
}
