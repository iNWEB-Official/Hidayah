package com.hidayah.app

import com.hidayah.app.data.*
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.CancellationException
import org.junit.Assert.*
import org.junit.Test

class PrayerCalculationTest {
    private val today = LocalDate.of(2025, 3, 15)
    private val point = GeoPoint(23.8103, 90.4125)
    private val zone = ZoneId.of("Asia/Dhaka")
    private val settings = PrayerSettings()
    private val times = listOf(Prayer("Fajr", "05:00"), Prayer("Maghrib", "18:00"))

    private class FakeCalculator : PrayerCalculator {
        var calculations = 0
        var failRamadan = false
        var failToday = false
        var failBearing = false
        var cancel = false
        override fun calculate(point: GeoPoint, date: LocalDate, zone: ZoneId, settings: PrayerSettings): List<Prayer>? {
            calculations++
            if (cancel) throw CancellationException("cancelled")
            if (failToday && calculations == 1) throw IllegalStateException("daily unavailable")
            if (failRamadan && calculations > 1) throw IllegalStateException("timetable unavailable")
            return listOf(Prayer("Fajr", "05:00"), Prayer("Maghrib", "18:00"))
        }
        override fun qiblaBearing(point: GeoPoint): Double? {
            if (failBearing) throw IllegalStateException("bearing unavailable")
            return 277.0
        }
    }

    @Test fun successfulCalculationsIncludeDailyAndRamadanTimes() {
        val result = calculatePrayerData(FakeCalculator(), point, today, zone, settings)
        assertEquals(times, result.prayers)
        assertEquals(277.0, result.qiblaBearing!!, 0.001)
        assertTrue(result.ramadan!!.days.size in 29..30)
        assertEquals("05:00", result.ramadan!!.days.first().sehriEnd)
        assertEquals("18:00", result.ramadan!!.days.first().iftar)
    }

    @Test fun ramadanFailureDoesNotEraseDailyTimesOrQibla() {
        val calc = FakeCalculator().apply { failRamadan = true }
        val result = calculatePrayerData(calc, point, today, zone, settings)
        assertEquals(times, result.prayers)
        assertEquals(277.0, result.qiblaBearing!!, 0.001)
        assertNull(result.ramadan)
    }

    @Test fun qiblaFailureDoesNotErasePrayerTimesOrRamadan() {
        val calc = FakeCalculator().apply { failBearing = true }
        val result = calculatePrayerData(calc, point, today, zone, settings)
        assertEquals(times, result.prayers)
        assertNull(result.qiblaBearing)
        assertNotNull(result.ramadan)
    }

    @Test fun dailyFailureDoesNotEraseOtherResults() {
        val calc = FakeCalculator().apply { failToday = true }
        val result = calculatePrayerData(calc, point, today, zone, settings)
        assertNull(result.prayers)
        assertEquals(277.0, result.qiblaBearing!!, 0.001)
        assertNotNull(result.ramadan)
    }

    @Test(expected = CancellationException::class)
    fun coroutineCancellationIsNotSwallowed() {
        calculatePrayerData(FakeCalculator().apply { cancel = true }, point, today, zone, settings)
    }
}
