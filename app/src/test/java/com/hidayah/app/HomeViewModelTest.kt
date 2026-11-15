package com.hidayah.app

import com.hidayah.app.data.DailyValue
import com.hidayah.app.data.DailyValueRepository
import com.hidayah.app.data.DailyValueStatus
import com.hidayah.app.ui.HomeViewModel
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private class FakeDailyRepository : DailyValueRepository {
        var calls = 0
        var fail = false
        var value: DailyValueStatus = DailyValueStatus.Missing(false)
        override fun observe(day: LocalDate): Flow<DailyValueStatus> = flow {
            calls++
            if (fail) throw IllegalStateException("unavailable")
            emit(value)
        }
    }
    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun teardown() { Dispatchers.resetMain() }

    @Test fun retryAfterMissingReloadsContent() = runTest(dispatcher) {
        val repo = FakeDailyRepository()
        val vm = HomeViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.collect() }
        runCurrent()
        assertEquals(DailyValueStatus.Missing(false), vm.state.value.dailyValue)
        val value = DailyValue("শিরোনাম", "ভাবনা", "Ayat", "বাংলা", "সূত্র")
        repo.value = DailyValueStatus.Available(value, false)
        vm.retry(); runCurrent()
        assertEquals(DailyValueStatus.Available(value, false), vm.state.value.dailyValue)
        assertEquals(2, repo.calls)
    }

    @Test fun retryCancelsOldLiveListenerAndUsesNewState() = runTest(dispatcher) {
        var calls = 0
        var cancelled = 0
        val repo = object : DailyValueRepository {
            override fun observe(day: LocalDate): Flow<DailyValueStatus> = flow {
                calls++
                if (calls == 1) {
                    try { emit(DailyValueStatus.Missing(true)); awaitCancellation() } finally { cancelled++ }
                } else emit(DailyValueStatus.Invalid)
            }
        }
        val vm = HomeViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.collect() }
        runCurrent(); assertEquals(DailyValueStatus.Missing(true), vm.state.value.dailyValue)
        vm.retry(); runCurrent()
        assertEquals(1, cancelled)
        assertEquals(2, calls)
        assertEquals(DailyValueStatus.Invalid, vm.state.value.dailyValue)
    }

    @Test fun liveFallbackMayEmitFreshContentWithoutChangingQuranSource() = runTest(dispatcher) {
        val updates = MutableStateFlow<DailyValueStatus>(DailyValueStatus.Missing(true))
        val repo = object : DailyValueRepository { override fun observe(day: LocalDate) = updates }
        val vm = HomeViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.collect() }
        runCurrent()
        val fallback = DailyValueStatus.Available(DailyValue("Fallback", "Reflection", "Ayat", "Text", "Source"), false, isFallback = true)
        updates.value = fallback; runCurrent()
        assertEquals(fallback, vm.state.value.dailyValue)
    }

    @Test fun synchronousObservationFailureIsReportedAndRetriable() = runTest(dispatcher) {
        var fail = true
        val repo = object : DailyValueRepository {
            override fun observe(day: LocalDate): Flow<DailyValueStatus> {
                if (fail) error("setup failed")
                return flow { emit(DailyValueStatus.Missing(false)) }
            }
        }
        val vm = HomeViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.collect() }
        runCurrent(); assertTrue(vm.state.value.dailyValue is DailyValueStatus.Error)
        fail = false; vm.retry(); runCurrent()
        assertEquals(DailyValueStatus.Missing(false), vm.state.value.dailyValue)
    }

    @Test fun thrownFallbackErrorCanBeRetriedAfterNetworkRecovery() = runTest(dispatcher) {
        val repo = FakeDailyRepository().apply { fail = true }
        val vm = HomeViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.collect() }
        runCurrent()
        assertTrue(vm.state.value.dailyValue is DailyValueStatus.Error)
        repo.fail = false
        repo.value = DailyValueStatus.Invalid
        vm.retry(); runCurrent()
        assertEquals(DailyValueStatus.Invalid, vm.state.value.dailyValue)
        assertEquals(2, repo.calls)
    }
}
