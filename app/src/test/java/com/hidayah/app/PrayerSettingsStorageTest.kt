package com.hidayah.app

import com.hidayah.app.data.*
import org.junit.Assert.assertEquals
import org.junit.Test

class PrayerSettingsStorageTest {
    @Test fun missingAndUnknownNamesUseDefaultsIndependently() {
        assertEquals(PrayerSettings(), decodePrayerSettings(null, null))
        assertEquals(PrayerSettings(PrayerMethod.KARACHI, AsrMethod.STANDARD), decodePrayerSettings("KARACHI", "removed"))
        assertEquals(PrayerSettings(PrayerMethod.MUSLIM_WORLD_LEAGUE, AsrMethod.HANAFI), decodePrayerSettings("invalid", "HANAFI"))
    }
    @Test fun recognizedNamesRoundTrip() {
        for (method in PrayerMethod.entries) for (asr in AsrMethod.entries)
            assertEquals(PrayerSettings(method, asr), decodePrayerSettings(method.name, asr.name))
    }
}
