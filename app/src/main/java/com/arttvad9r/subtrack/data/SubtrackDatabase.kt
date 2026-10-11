package com.arttvad9r.subtrack.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SubscriptionEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class SubtrackDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
}
