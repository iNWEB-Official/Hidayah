package com.hidayah.app.data

import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.CancellationException

/** Independent results: a timetable or compass failure must not erase valid daily prayer times. */
data class PrayerCalculation(
    val prayers: List<Prayer>?,
    val qiblaBearing: Double?,
    val ramadan: RamadanTimetable?,
)

fun calculatePrayerData(
    calculator: PrayerCalculator,
    point: GeoPoint,
    date: LocalDate,
    zone: ZoneId,
    settings: PrayerSettings,
): PrayerCalculation {
    fun <T> attempt(block: () -> T): T? = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        null
    }
    val prayers = attempt { calculator.calculate(point, date, zone, settings) }
    val bearing = attempt { calculator.qiblaBearing(point) }
    val ramadan = attempt { RamadanSchedule(calculator).forCurrentOrNextRamadan(date, point, zone, settings) }
    return PrayerCalculation(prayers, bearing, ramadan)
}
