package com.hidayah.app

import com.hidayah.app.data.*
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import org.junit.Assert.*
import org.junit.Test

class RepositoriesTest {
    @Test fun learningContentHasSourcesAndValidAnswers() {
        assertTrue(ValueTopics.all { it.source.isNotBlank() })
        assertTrue(DuaItems.all { it.arabic.isNotBlank() && it.meaning.isNotBlank() && it.sourceUrl.startsWith("https://") })
        assertTrue(Stories.all { it.source.isNotBlank() && it.url.startsWith("https://") })
        assertTrue(QuizQuestions.all { it.correct in it.answers.indices && it.source.isNotBlank() })
    }

    @Test fun trackerShowsSevenDeedsAndSeparatesDates() {
        val today = LocalDate.of(2026, 11, 13)
        val records = listOf(DeedRecord("2026-11-13", "fajr", true), DeedRecord("2026-11-12", "isha", true),
            DeedRecord("2026-11-13", "quran", false), DeedRecord("2026-11-13", "unknown", true))
        val state = trackerSnapshot(today, records)
        assertEquals(7, state.habits.size)
        assertTrue(state.habits.single { it.id == "fajr" }.completed)
        assertFalse(state.habits.single { it.id == "isha" }.completed)
        assertEquals(1, state.history[0].completed)
        assertEquals(1, state.history[1].completed)
        assertEquals(7, state.history.size)
    }

    @Test fun dailyValueRejectsIncompleteOrUnattributedContent() {
        val valid = mapOf("title" to "Patience", "reflection" to "A reflection", "referenceType" to "Ayat", "referenceText" to "Verified text", "reference" to "Verified reference")
        assertNotNull(parseDailyValue(valid))
        assertNull(parseDailyValue(valid - "reference"))
        assertNull(parseDailyValue(valid + ("referenceType" to "unknown")))
    }

    // Example values from Adhan's upstream Java test suite (Raleigh, 12 July 2015).
    @Test fun adhanTimesUseSpecifiedZoneAndMadhab() {
        val previous = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)
            val prayers = AdhanPrayerCalculator().calculate(
                GeoPoint(35.775, -78.6336), LocalDate.of(2015, 7, 12), ZoneId.of("America/New_York"),
                PrayerSettings(PrayerMethod.NORTH_AMERICA, AsrMethod.HANAFI),
            )!!
            assertEquals(listOf("4:42 AM", "1:21 PM", "6:22 PM", "8:32 PM", "9:57 PM"), prayers.map { it.time })
        } finally { Locale.setDefault(previous) }
    }

    @Test fun polarDayReturnsNoTimes() {
        assertNull(AdhanPrayerCalculator().calculate(GeoPoint(89.0, 0.0), LocalDate.of(2025, 6, 21), ZoneId.of("UTC"), PrayerSettings()))
    }

    @Test fun qiblaIsBearingFromTrueNorth() {
        assertEquals(284.6286, AdhanPrayerCalculator().qiblaBearing(GeoPoint(17.9757, 102.6331))!!, 0.01)
        assertNull(AdhanPrayerCalculator().qiblaBearing(GeoPoint(21.4225241, 39.8261818)))
    }

    @Test fun ramadanDaysUseFajrAndMaghribAndCoverCurrentMonth() {
        val timetable = RamadanSchedule(AdhanPrayerCalculator()).forCurrentOrNextRamadan(
            LocalDate.of(2025, 3, 15), GeoPoint(23.8103, 90.4125), ZoneId.of("Asia/Dhaka"), PrayerSettings(),
        )
        assertEquals(1446, timetable.hijriYear)
        assertTrue(timetable.days.size in 29..30)
        assertTrue(timetable.days.any { it.date == LocalDate.of(2025, 3, 15) })
        val day = timetable.days.first()
        val prayers = AdhanPrayerCalculator().calculate(GeoPoint(23.8103, 90.4125), day.date, ZoneId.of("Asia/Dhaka"), PrayerSettings())!!
        assertEquals(prayers.single { it.name == "Fajr" }.time, day.sehriEnd)
        assertEquals(prayers.single { it.name == "Maghrib" }.time, day.iftar)
    }

    @Test fun ramadanScheduleSelectsNextRamadanOutsideMonth() {
        val today = LocalDate.of(2025, 5, 1)
        val schedule = RamadanSchedule(AdhanPrayerCalculator()).forCurrentOrNextRamadan(today, GeoPoint(23.8103, 90.4125), ZoneId.of("Asia/Dhaka"), PrayerSettings())
        assertTrue(schedule.days.first().date.isAfter(today))
        assertEquals(1447, schedule.hijriYear)
    }
}
