package com.hidayah.app.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hidayah.app.data.*
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PrayerUiState(
    val prayers: List<Prayer> = emptyList(),
    val point: GeoPoint? = null,
    val qiblaBearing: Double? = null,
    val settings: PrayerSettings = PrayerSettings(),
    val date: LocalDate = LocalDate.now(),
    val zone: ZoneId = ZoneId.systemDefault(),
    val loading: Boolean = false,
    val error: String? = null,
    val ramadan: RamadanTimetable? = null,
    val ramadanLoading: Boolean = false,
    val ramadanError: String? = null,
)

class PrayerViewModel(private val calculator: PrayerCalculator, private val locationSource: DeviceLocationSource) : ViewModel() {
    private val mutableState = MutableStateFlow(PrayerUiState())
    val state: StateFlow<PrayerUiState> = mutableState.asStateFlow()
    private var calculationJob: kotlinx.coroutines.Job? = null

    fun refreshLocation() {
        if (mutableState.value.loading) return
        viewModelScope.launch {
            mutableState.update { it.copy(loading = true, error = null) }
            locationSource.currentLocation().fold(
                onSuccess = { point ->
                    mutableState.update { it.copy(point = point, loading = false) }
                    recalculate(force = true)
                },
                onFailure = { error ->
                    calculationJob?.cancel()
                    mutableState.update { it.copy(point = null, qiblaBearing = null, prayers = emptyList(), ramadan = null, ramadanLoading = false, loading = false, error = error.message) }
                },
            )
        }
    }
    fun permissionDenied() {
        calculationJob?.cancel()
        mutableState.update { it.copy(point = null, qiblaBearing = null, prayers = emptyList(), ramadan = null, ramadanLoading = false, error = "Location permission denied. You can allow it in system settings.") }
    }
    fun setMethod(method: PrayerMethod) { mutableState.update { it.copy(settings = it.settings.copy(method = method)) }; recalculate(force = true) }
    fun setAsr(asr: AsrMethod) { mutableState.update { it.copy(settings = it.settings.copy(asr = asr)) }; recalculate(force = true) }

    fun recalculate(force: Boolean = false) {
        val old = mutableState.value
        val zone = ZoneId.systemDefault()
        val date = LocalDate.now(zone)
        val point = old.point
        if (!force && date == old.date && zone == old.zone) return
        calculationJob?.cancel()
        if (point == null) {
            mutableState.update { it.copy(date = date, zone = zone, ramadan = null, ramadanLoading = false) }
            return
        }
        mutableState.update { it.copy(date = date, zone = zone, prayers = emptyList(), qiblaBearing = null, ramadan = null, ramadanLoading = true, ramadanError = null) }
        calculationJob = viewModelScope.launch {
            try {
                val result = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
                    calculatePrayerData(calculator, point, date, zone, old.settings)
                }
                mutableState.update { it.copy(prayers = result.prayers ?: emptyList(), qiblaBearing = result.qiblaBearing,
                    ramadan = result.ramadan, ramadanLoading = false,
                    error = if (result.prayers == null) "Times are unavailable for this location and date. Check a trusted local timetable." else null,
                    ramadanError = if (result.ramadan == null) "Ramadan timetable unavailable." else null) }
            } catch (e: kotlinx.coroutines.CancellationException) { throw e }
            catch (_: Exception) {
                mutableState.update { it.copy(prayers = emptyList(), qiblaBearing = null, ramadan = null, ramadanLoading = false,
                    error = "Could not calculate times for this location. Try again.", ramadanError = "Ramadan timetable unavailable.") }
            }
        }
    }
}

class HidayahViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        HomeViewModel::class.java -> HomeViewModel(AppContainer.dailyValueRepository)
        QuranViewModel::class.java -> QuranViewModel(AppContainer.quranRepository)
        QuranReaderViewModel::class.java -> QuranReaderViewModel(AppContainer.quranRepository)
        ValuesViewModel::class.java -> ValuesViewModel(AppContainer.quranRepository)
        PrayerViewModel::class.java -> PrayerViewModel(AppContainer.prayerCalculator, DeviceLocationSource(context))
        TrackerViewModel::class.java -> TrackerViewModel(AppContainer.trackerRepository(context))
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    } as T
}
