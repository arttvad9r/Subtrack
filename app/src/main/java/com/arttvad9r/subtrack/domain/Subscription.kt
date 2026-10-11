package com.arttvad9r.subtrack.domain

import java.time.LocalDate

enum class BillingPeriod {
    NONE,
    MONTHLY,
    YEARLY,
}

data class Subscription(
    val id: Long = 0,
    val name: String,
    val priceMinor: Long,
    val currency: String,
    val date: LocalDate,
    val billingPeriod: BillingPeriod,
    val autoRenew: Boolean,
    val reminderDaysBefore: Int,
    val note: String = "",
    val url: String = "",
)
