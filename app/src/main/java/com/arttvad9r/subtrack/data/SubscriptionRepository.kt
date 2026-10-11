package com.arttvad9r.subtrack.data

import com.arttvad9r.subtrack.domain.Subscription
import com.arttvad9r.subtrack.domain.normalized
import com.arttvad9r.subtrack.reminder.SubscriptionReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SubscriptionRepository(
    private val dao: SubscriptionDao,
    private val reminderScheduler: SubscriptionReminderScheduler,
) {
    val subscriptions: Flow<List<Subscription>> = dao.observeAll().map { entities ->
        entities.map(SubscriptionEntity::toDomain)
    }

    suspend fun save(subscription: Subscription): Long {
        val clean = normalized(subscription).copy(
            name = subscription.name.trim(),
            currency = subscription.currency.trim().uppercase(),
            note = subscription.note.trim(),
            url = subscription.url.trim(),
        )

        val id = if (clean.id == 0L) {
            dao.insert(clean.toEntity())
        } else {
            dao.update(clean.toEntity())
            clean.id
        }

        reminderScheduler.schedule(clean.copy(id = id))
        return id
    }

    suspend fun delete(subscription: Subscription) {
        dao.delete(subscription.toEntity())
        reminderScheduler.cancel(subscription.id)
    }

    suspend fun reconcileAndScheduleAll() {
        dao.getAll().forEach { entity ->
            val original = entity.toDomain()
            val clean = normalized(original)
            if (clean.date != original.date) {
                dao.updateNextDate(clean.id, clean.date.toString())
            }
            reminderScheduler.schedule(clean, catchUpIfPast = false)
        }
    }
}
