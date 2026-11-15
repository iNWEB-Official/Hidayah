package com.hidayah.app

import com.hidayah.app.data.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import org.junit.Assert.*
import org.junit.Test

class PrayerZoneMethodTest {
    @Test fun daylightSavingZoneFormattingAndAsrChoicesAreApplied() {
        val old = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)
            val calc = AdhanPrayerCalculator()
            val point = GeoPoint(40.7128, -74.0060)
            val date = LocalDate.of(2025, 3, 9)
            val standard = PrayerSettings(PrayerMethod.NORTH_AMERICA, AsrMethod.STANDARD)
            val hanafi = standard.copy(asr = AsrMethod.HANAFI)
            val ny = calc.calculate(point, date, ZoneId.of("America/New_York"), standard)!!
            val utc = calc.calculate(point, date, ZoneId.of("UTC"), standard)!!
            val hanafiTimes = calc.calculate(point, date, ZoneId.of("America/New_York"), hanafi)!!
            val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
            fun time(prayers: List<Prayer>, name: String) = LocalTime.parse(prayers.single { it.name == name }.time, formatter)
            assertEquals(time(utc, "Dhuhr").minusHours(4), time(ny, "Dhuhr"))
            assertTrue(time(hanafiTimes, "Asr").isAfter(time(ny, "Asr")))
        } finally { Locale.setDefault(old) }
    }
    @Test fun directionAtKaabaAndAntipodeIsUndefined() {
        val calculator = AdhanPrayerCalculator()
        assertNull(calculator.qiblaBearing(GeoPoint(21.4225241, 39.8261818)))
        assertNull(calculator.qiblaBearing(GeoPoint(-21.4225241, -140.1738182)))
    }
}
