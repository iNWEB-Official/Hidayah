package com.hidayah.app.data

import java.time.LocalDate
import java.time.ZoneId
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField
import kotlinx.coroutines.CancellationException

data class RamadanDay(val day: Int, val date: LocalDate, val sehriEnd: String?, val iftar: String?)
data class RamadanTimetable(val hijriYear: Int, val days: List<RamadanDay>)

/** Dates are Umm al-Qura estimates, not a local moon-sighting determination. */
class RamadanSchedule(private val calculator: PrayerCalculator) {
    fun forCurrentOrNextRamadan(today: LocalDate, point: GeoPoint, zone: ZoneId, settings: PrayerSettings): RamadanTimetable {
        fun isRamadan(date: LocalDate) = HijrahDate.from(date).get(ChronoField.MONTH_OF_YEAR) == 9
        var first = today
        if (isRamadan(first)) while (isRamadan(first.minusDays(1))) first = first.minusDays(1)
        else while (!isRamadan(first)) first = first.plusDays(1)
        val year = HijrahDate.from(first).get(ChronoField.YEAR_OF_ERA)
        val days = mutableListOf<RamadanDay>()
        var current = first
        while (isRamadan(current)) {
            val prayers = try {
                calculator.calculate(point, current, zone, settings)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                null
            }
            days.add(RamadanDay(days.size + 1, current,
                prayers?.firstOrNull { it.name == "Fajr" }?.time,
                prayers?.firstOrNull { it.name == "Maghrib" }?.time))
            current = current.plusDays(1)
        }
        check(days.any { it.sehriEnd != null || it.iftar != null }) { "No Ramadan times available" }
        return RamadanTimetable(year, days)
    }
}
