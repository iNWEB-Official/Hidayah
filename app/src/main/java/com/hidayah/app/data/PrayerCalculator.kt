package com.hidayah.app.data

import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.Qibla
import com.batoulapps.adhan.data.DateComponents
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double = 0.0,
    val accuracyMeters: Float? = null,
)

enum class PrayerMethod(val label: String, val adhan: CalculationMethod) {
    MUSLIM_WORLD_LEAGUE("Muslim World League", CalculationMethod.MUSLIM_WORLD_LEAGUE),
    KARACHI("Karachi", CalculationMethod.KARACHI),
    SINGAPORE("Singapore", CalculationMethod.SINGAPORE),
    EGYPTIAN("Egyptian", CalculationMethod.EGYPTIAN),
    NORTH_AMERICA("North America (ISNA)", CalculationMethod.NORTH_AMERICA),
}

enum class AsrMethod(val label: String, val adhan: Madhab) {
    STANDARD("Standard (Shafi)", Madhab.SHAFI),
    HANAFI("Hanafi", Madhab.HANAFI),
}

data class PrayerSettings(
    val method: PrayerMethod = PrayerMethod.MUSLIM_WORLD_LEAGUE,
    val asr: AsrMethod = AsrMethod.STANDARD,
)

interface PrayerCalculator {
    /** Null means the library could not calculate this date and location (e.g. polar day). */
    fun calculate(point: GeoPoint, date: LocalDate, zone: ZoneId, settings: PrayerSettings): List<Prayer>?
    /** Degrees clockwise from true north. */
    fun qiblaBearing(point: GeoPoint): Double?
}

class AdhanPrayerCalculator : PrayerCalculator {
    override fun calculate(point: GeoPoint, date: LocalDate, zone: ZoneId, settings: PrayerSettings): List<Prayer>? {
        require(point.latitude in -90.0..90.0 && point.longitude in -180.0..180.0)
        val params = settings.method.adhan.parameters.apply { madhab = settings.asr.adhan }
        val times = PrayerTimes(
            Coordinates(point.latitude, point.longitude),
            DateComponents(date.year, date.monthValue, date.dayOfMonth), params,
        )
        // Adhan returns null for all times where calculation is impossible.
        val values = listOf(times.fajr, times.dhuhr, times.asr, times.maghrib, times.isha)
        if (values.any { it == null }) return null
        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()).withZone(zone)
        return listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
            .zip(values) { name, value -> Prayer(name, formatter.format(requireNotNull(value).toInstant())) }
    }

    override fun qiblaBearing(point: GeoPoint): Double? {
        // A direction is undefined at the Kaaba and its antipode.
        val toRadians = PI / 180.0
        val lat = point.latitude * toRadians
        val kaabaLat = 21.4225241 * toRadians
        val deltaLon = (point.longitude - 39.8261818) * toRadians
        val centralAngle = acos((sin(lat) * sin(kaabaLat) + cos(lat) * cos(kaabaLat) * cos(deltaLon)).coerceIn(-1.0, 1.0))
        if (centralAngle < 0.00001 || PI - centralAngle < 0.00001) return null
        return Qibla(Coordinates(point.latitude, point.longitude)).direction.takeIf { it.isFinite() }
    }
}
