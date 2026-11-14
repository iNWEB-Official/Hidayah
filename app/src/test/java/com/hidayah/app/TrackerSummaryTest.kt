package com.hidayah.app

import com.hidayah.app.data.DeedDefinitions
import com.hidayah.app.data.DeedRecord
import com.hidayah.app.data.trackerSnapshot
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class TrackerSummaryTest {
    @Test fun emptyDataShowsSevenUncheckedHabitsAndSevenDays() {
        val today = LocalDate.of(2026, 1, 3)
        val snapshot = trackerSnapshot(today, emptyList())
        assertEquals(7, snapshot.habits.size)
        assertTrue(snapshot.habits.none { it.completed })
        assertEquals((0L..6L).map { today.minusDays(it) }, snapshot.history.map { it.date })
        assertTrue(snapshot.history.all { it.completed == 0 && it.total == 7 })
    }

    @Test fun countsOnlyKnownCompletedDeedsWithinEachDay() {
        val today = LocalDate.of(2026, 1, 3)
        val records = listOf(
            DeedRecord(today.toString(), "fajr", true),
            DeedRecord(today.toString(), "quran", false),
            DeedRecord(today.toString(), "not_a_deed", true),
            DeedRecord(today.minusDays(6).toString(), "isha", true),
            DeedRecord(today.minusDays(7).toString(), "fajr", true),
            DeedRecord(today.plusDays(1).toString(), "asr", true),
        )
        val snapshot = trackerSnapshot(today, records)
        assertEquals(1, snapshot.history.first().completed)
        assertEquals(1, snapshot.history.last().completed)
        assertEquals(2, snapshot.history.sumOf { it.completed })
        assertTrue(snapshot.habits.single { it.id == "fajr" }.completed)
        assertFalse(snapshot.habits.single { it.id == "quran" }.completed)
    }

    @Test fun completeDayIsSevenOfSeven() {
        val today = LocalDate.of(2024, 2, 29)
        val records = DeedDefinitions.map { DeedRecord(today.toString(), it.id, true) }
        val snapshot = trackerSnapshot(today, records)
        assertTrue(snapshot.habits.all { it.completed })
        assertEquals(7, snapshot.history.first().completed)
        assertEquals(LocalDate.of(2024, 2, 23), snapshot.history.last().date)
    }

    @Test fun duplicateCompletedRowsCannotInflateHistory() {
        val today = LocalDate.of(2026, 11, 13)
        val record = DeedRecord(today.toString(), "fajr", true)
        val snapshot = trackerSnapshot(today, listOf(record, record))
        assertEquals(1, snapshot.history.first().completed)
    }
}
