package com.arttvad9r.subtrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arttvad9r.subtrack.domain.BillingPeriod
import com.arttvad9r.subtrack.domain.Subscription
import java.time.LocalDate

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val priceMinor: Long,
    val currency: String,
    val nextDate: String,
    val billingPeriod: String,
    val autoRenew: Boolean,
    val reminderDaysBefore: Int,
    val note: String,
    val url: String,
)

fun SubscriptionEntity.toDomain(): Subscription = Subscription(
    id = id,
    name = name,
    priceMinor = priceMinor,
    currency = currency,
    date = LocalDate.parse(nextDate),
    billingPeriod = BillingPeriod.valueOf(billingPeriod),
    autoRenew = autoRenew,
    reminderDaysBefore = reminderDaysBefore,
    note = note,
    url = url,
)

fun Subscription.toEntity(): SubscriptionEntity = SubscriptionEntity(
    id = id,
    name = name,
    priceMinor = priceMinor,
    currency = currency.uppercase(),
    nextDate = date.toString(),
    billingPeriod = billingPeriod.name,
    autoRenew = autoRenew,
    reminderDaysBefore = reminderDaysBefore,
    note = note,
    url = url,
)
