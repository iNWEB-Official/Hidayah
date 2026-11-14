package com.hidayah.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hidayah.app.data.HidayahDatabase
import com.hidayah.app.data.RoomTrackerRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrackerDatabaseTest {
    private lateinit var db: HidayahDatabase
    @Before fun create() { db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), HidayahDatabase::class.java).build() }
    @After fun close() = db.close()
    @Test fun recordsArePerDayAndUpdateExistingEntry() = runBlocking {
        val repo = RoomTrackerRepository(db.deedDao())
        val day = LocalDate.of(2026, 11, 13)
        repo.setCompleted(day, "fajr", true)
        repo.setCompleted(day.minusDays(1), "fajr", true)
        assertTrue(repo.observeBetween(day, day).first().single().completed)
        repo.setCompleted(day, "fajr", false)
        val rows = repo.observeBetween(day.minusDays(1), day).first()
        assertEquals(2, rows.size)
        assertFalse(rows.single { it.day == day.toString() }.completed)
        assertTrue(rows.single { it.day == day.minusDays(1).toString() }.completed)
    }
}
