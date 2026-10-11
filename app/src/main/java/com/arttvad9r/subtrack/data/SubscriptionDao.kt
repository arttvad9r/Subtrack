package com.arttvad9r.subtrack.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY nextDate ASC, name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions ORDER BY nextDate ASC, name COLLATE NOCASE ASC")
    suspend fun getAll(): List<SubscriptionEntity>

    @Insert
    suspend fun insert(subscription: SubscriptionEntity): Long

    @Update
    suspend fun update(subscription: SubscriptionEntity)

    @Delete
    suspend fun delete(subscription: SubscriptionEntity)

    @Query("UPDATE subscriptions SET nextDate = :nextDate WHERE id = :id")
    suspend fun updateNextDate(id: Long, nextDate: String)
}
