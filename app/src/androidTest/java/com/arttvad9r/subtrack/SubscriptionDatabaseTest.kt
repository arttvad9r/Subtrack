package com.arttvad9r.subtrack

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arttvad9r.subtrack.data.SubtrackDatabase
import com.arttvad9r.subtrack.data.SubscriptionEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SubscriptionDatabaseTest {
    private lateinit var database: SubtrackDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, SubtrackDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertUpdateDeleteRoundTrip() = runBlocking {
        val dao = database.subscriptionDao()
        val id = dao.insert(sample())

        val inserted = dao.getAll().single()
        assertEquals(id, inserted.id)
        assertEquals("ChatGPT Plus", inserted.name)
        assertEquals(2_000L, inserted.priceMinor)

        dao.update(inserted.copy(priceMinor = 2_500L, note = "Official site"))
        val updated = dao.getAll().single()
        assertEquals(2_500L, updated.priceMinor)
        assertEquals("Official site", updated.note)

        dao.delete(updated)
        assertTrue(dao.getAll().isEmpty())
    }

    private fun sample() = SubscriptionEntity(
        name = "ChatGPT Plus",
        priceMinor = 2_000L,
        currency = "USD",
        nextDate = "2026-11-11",
        billingPeriod = "MONTHLY",
        autoRenew = true,
        reminderDaysBefore = 3,
        note = "",
        url = "https://chatgpt.com",
    )
}
