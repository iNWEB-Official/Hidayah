package com.hidayah.app

import com.hidayah.app.data.DeedRecord
import com.hidayah.app.data.TrackerRepository
import com.hidayah.app.ui.TrackerViewModel
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TrackerViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private class FakeRepository : TrackerRepository {
        val records = MutableStateFlow<List<DeedRecord>>(emptyList())
        var failRead = false
        var failReadImmediately = false
        var failWrite = false
        var observedRange: Pair<LocalDate, LocalDate>? = null
        override fun observeBetween(first: LocalDate, last: LocalDate): Flow<List<DeedRecord>> {
            observedRange = first to last
            if (failReadImmediately) throw IllegalStateException("read setup failed")
            return if (failRead) flow { throw IllegalStateException("read failed") } else records.map { rows ->
                rows.filter { it.day >= first.toString() && it.day <= last.toString() }
            }
        }
        override suspend fun setCompleted(day: LocalDate, id: String, completed: Boolean) {
            if (failWrite) throw IllegalStateException("write failed")
            records.value = records.value.filterNot { it.day == day.toString() && it.deedId == id } +
                DeedRecord(day.toString(), id, completed)
        }
    }
    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun teardown() { Dispatchers.resetMain() }

    @Test fun observesSevenDaysAndReflectsSuccessfulWrites() = runTest(dispatcher) {
        val repo = FakeRepository()
        val vm = TrackerViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.collect() }
        runCurrent()
        val today = LocalDate.now()
        assertEquals(today.minusDays(6) to today, repo.observedRange)
        assertFalse(vm.state.value.loading)
        vm.setCompleted("fajr", true); runCurrent()
        assertTrue(vm.state.value.snapshot.habits.single { it.id == "fajr" }.completed)
        assertEquals(1, vm.state.value.snapshot.history.first().completed)
        vm.setCompleted("fajr", false); runCurrent()
        assertFalse(vm.state.value.snapshot.habits.single { it.id == "fajr" }.completed)
        assertEquals(0, vm.state.value.snapshot.history.first().completed)
    }

    @Test fun writeFailureIsShownAndSuccessfulRetryClearsIt() = runTest(dispatcher) {
        val repo = FakeRepository()
        val vm = TrackerViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.collect() }
        runCurrent()
        repo.failWrite = true
        vm.setCompleted("fajr", true); runCurrent()
        assertNotNull(vm.state.value.error)
        assertTrue(repo.records.value.isEmpty())
        repo.failWrite = false
        vm.setCompleted("fajr", true); runCurrent()
        assertNull(vm.state.value.error)
        assertTrue(vm.state.value.snapshot.habits.single { it.id == "fajr" }.completed)
    }

    @Test fun readFailureStopsLoadingAndReportsError() = runTest(dispatcher) {
        val repo = FakeRepository().apply { failRead = true }
        val vm = TrackerViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.collect() }
        runCurrent()
        assertFalse(vm.state.value.loading)
        assertTrue(vm.state.value.readFailed)
        assertNotNull(vm.state.value.error)
        assertTrue(vm.state.value.snapshot.habits.none { it.completed })
        repo.failRead = false
        repo.records.value = listOf(DeedRecord(LocalDate.now().toString(), "fajr", true))
        vm.retryRead(); runCurrent()
        assertFalse(vm.state.value.readFailed)
        assertNull(vm.state.value.error)
        assertTrue(vm.state.value.snapshot.habits.single { it.id == "fajr" }.completed)
    }

    @Test fun synchronousReadFailureCanAlsoBeRetried() = runTest(dispatcher) {
        val repo = FakeRepository().apply { failReadImmediately = true }
        val vm = TrackerViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.collect() }
        runCurrent()
        assertTrue(vm.state.value.readFailed)
        repo.failReadImmediately = false
        vm.retryRead(); runCurrent()
        assertFalse(vm.state.value.readFailed)
        assertFalse(vm.state.value.loading)
    }

    @Test fun dayChangeCancelsOldObservation() = runTest(dispatcher) {
        var day = LocalDate.of(2026, 12, 31)
        val cancelled = mutableListOf<LocalDate>()
        val repo = object : TrackerRepository {
            override fun observeBetween(first: LocalDate, last: LocalDate): Flow<List<DeedRecord>> = flow {
                try { emit(emptyList()); awaitCancellation() } finally { cancelled.add(last) }
            }
            override suspend fun setCompleted(day: LocalDate, id: String, completed: Boolean) = Unit
        }
        val vm = TrackerViewModel(repo) { day }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.collect() }
        runCurrent()
        val previous = day
        day = day.plusDays(1)
        vm.refreshDate(); runCurrent()
        assertTrue(previous in cancelled)
        assertEquals(day, vm.state.value.snapshot.date)
    }

    @Test fun dayChangeObservesNewSevenDayWindowAndWritesNewDay() = runTest(dispatcher) {
        var day = LocalDate.of(2026, 12, 31)
        val repo = FakeRepository()
        val vm = TrackerViewModel(repo) { day }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.collect() }
        runCurrent()
        vm.setCompleted("fajr", true); runCurrent()
        assertEquals(1, vm.state.value.snapshot.history.first().completed)
        day = day.plusDays(1)
        vm.refreshDate(); runCurrent()
        assertEquals(day.minusDays(6) to day, repo.observedRange)
        assertEquals(day, vm.state.value.snapshot.date)
        assertFalse(vm.state.value.snapshot.habits.single { it.id == "fajr" }.completed)
        assertEquals(1, vm.state.value.snapshot.history[1].completed)
        vm.setCompleted("isha", true); runCurrent()
        assertTrue(repo.records.value.any { it.day == day.toString() && it.deedId == "isha" && it.completed })
        assertTrue(repo.records.value.any { it.day == day.minusDays(1).toString() && it.deedId == "fajr" && it.completed })
    }
}
