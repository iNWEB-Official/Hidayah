package com.hidayah.app

import com.hidayah.app.data.*
import com.hidayah.app.ui.PrayerViewModel
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrayerViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val point = GeoPoint(23.8103, 90.4125)
    private val dhaka = ZoneId.of("Asia/Dhaka")
    private val date = LocalDate.of(2025, 3, 15)
    private class FakeCalculator : PrayerCalculator {
        data class Call(val point: GeoPoint, val date: LocalDate, val zone: ZoneId, val settings: PrayerSettings)
        val calls = mutableListOf<Call>()
        override fun calculate(point: GeoPoint, date: LocalDate, zone: ZoneId, settings: PrayerSettings): List<Prayer> {
            calls.add(Call(point, date, zone, settings))
            return listOf(Prayer("Fajr", "05:00"), Prayer("Dhuhr", "12:00"), Prayer("Asr", "15:30"), Prayer("Maghrib", "18:00"), Prayer("Isha", "19:30"))
        }
        override fun qiblaBearing(point: GeoPoint): Double = 277.0
    }
    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun teardown() { Dispatchers.resetMain() }

    @Test fun locationSettingsAndDateZoneArePassedToCalculator() = runTest(dispatcher) {
        var day = date
        var zone = dhaka
        val calc = FakeCalculator()
        val vm = PrayerViewModel(calc, LocationSource { Result.success(point) }, dispatcher, { zone }, { day })
        vm.refreshLocation(); runCurrent()
        assertEquals(point, vm.state.value.point)
        assertEquals(5, vm.state.value.prayers.size)
        vm.setMethod(PrayerMethod.KARACHI); vm.setAsr(AsrMethod.HANAFI); runCurrent()
        assertEquals(PrayerSettings(PrayerMethod.KARACHI, AsrMethod.HANAFI), calc.calls.last().settings)
        day = day.plusDays(1); zone = ZoneId.of("Asia/Vientiane")
        vm.recalculate(); runCurrent()
        assertEquals(day, vm.state.value.date)
        assertEquals(zone, vm.state.value.zone)
        assertTrue(calc.calls.any { it.date == day && it.zone == zone && it.point == point })
    }

    @Test fun failureAndUnexpectedExceptionAllowRetryWithoutStaleTimes() = runTest(dispatcher) {
        var mode = 0
        val vm = PrayerViewModel(FakeCalculator(), LocationSource {
            when (mode) { 0 -> Result.success(point); 1 -> Result.failure(IllegalStateException("unavailable")); else -> error("unexpected") }
        }, dispatcher, { dhaka }, { date })
        vm.refreshLocation(); runCurrent()
        assertEquals(5, vm.state.value.prayers.size)
        mode = 1
        vm.refreshLocation()
        assertTrue(vm.state.value.loading)
        assertTrue(vm.state.value.prayers.isEmpty())
        runCurrent()
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.point)
        assertNotNull(vm.state.value.error)
        mode = 2; vm.refreshLocation(); runCurrent()
        assertFalse(vm.state.value.loading)
        assertNotNull(vm.state.value.error)
        mode = 0; vm.refreshLocation(); runCurrent()
        assertNull(vm.state.value.error)
        assertEquals(5, vm.state.value.prayers.size)
    }

    @Test fun settingsAreRestoredAndSaveFailuresAreVisible() = runTest(dispatcher) {
        var saved = PrayerSettings()
        var fail = false
        val store = object : PrayerSettingsStorage {
            override fun load(): PrayerSettings = saved
            override fun save(settings: PrayerSettings) { if (fail) error("failed"); saved = settings }
        }
        val source = LocationSource { Result.success(point) }
        val first = PrayerViewModel(FakeCalculator(), source, dispatcher, { dhaka }, { date }, store)
        first.setMethod(PrayerMethod.KARACHI)
        first.setAsr(AsrMethod.HANAFI)
        val next = PrayerViewModel(FakeCalculator(), source, dispatcher, { dhaka }, { date }, store)
        assertEquals(PrayerSettings(PrayerMethod.KARACHI, AsrMethod.HANAFI), next.state.value.settings)
        fail = true
        next.setMethod(PrayerMethod.EGYPTIAN)
        assertNotNull(next.state.value.settingsError)
        assertEquals(PrayerMethod.KARACHI, saved.method)
        fail = false
        next.setMethod(PrayerMethod.EGYPTIAN)
        assertNull(next.state.value.settingsError)
        assertEquals(PrayerMethod.EGYPTIAN, saved.method)
    }

    @Test fun permissionDenialCancelsInFlightLocationAndClearsState() = runTest(dispatcher) {
        val pending = CompletableDeferred<GeoPoint>()
        val vm = PrayerViewModel(FakeCalculator(), LocationSource { Result.success(pending.await()) }, dispatcher, { dhaka }, { date })
        vm.refreshLocation(); runCurrent()
        assertTrue(vm.state.value.loading)
        vm.permissionDenied(); pending.complete(point); runCurrent()
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.point)
        assertTrue(vm.state.value.prayers.isEmpty())
        assertNotNull(vm.state.value.error)
    }
}
