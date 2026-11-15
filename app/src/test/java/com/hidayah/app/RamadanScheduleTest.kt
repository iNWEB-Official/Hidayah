package com.hidayah.app

import com.hidayah.app.data.*
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.CancellationException
import org.junit.Assert.*
import org.junit.Test

class RamadanScheduleTest {
    private val day = LocalDate.of(2025, 3, 15)
    private val point = GeoPoint(23.8103, 90.4125)
    private val zone = ZoneId.of("Asia/Dhaka")
    private class Calculator : PrayerCalculator {
        var cancel = false
        override fun calculate(point: GeoPoint, date: LocalDate, zone: ZoneId, settings: PrayerSettings): List<Prayer>? {
            if (cancel) throw CancellationException("cancelled")
            if (date == LocalDate.of(2025, 3, 10)) error("not available")
            if (date == LocalDate.of(2025, 3, 11)) return null
            return listOf(Prayer("Fajr", "05:00"), Prayer("Maghrib", "18:00"))
        }
        override fun qiblaBearing(point: GeoPoint): Double? = null
    }
    @Test fun missingDatesRemainBlankWithoutHidingOtherDays() {
        val timetable = RamadanSchedule(Calculator()).forCurrentOrNextRamadan(day, point, zone, PrayerSettings())
        val missing = timetable.days.filter { it.date in listOf(LocalDate.of(2025, 3, 10), LocalDate.of(2025, 3, 11)) }
        assertEquals(2, missing.size)
        assertTrue(missing.all { it.sehriEnd == null && it.iftar == null })
        assertTrue(timetable.days.any { it.sehriEnd == "05:00" && it.iftar == "18:00" })
        assertTrue(timetable.days.size in 29..30)
    }
    @Test(expected = CancellationException::class)
    fun cancellationStillPropagates() {
        RamadanSchedule(Calculator().apply { cancel = true }).forCurrentOrNextRamadan(day, point, zone, PrayerSettings())
    }
}
