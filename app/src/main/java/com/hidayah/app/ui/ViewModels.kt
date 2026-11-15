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
    val settingsError: String? = null,
    val date: LocalDate = LocalDate.now(),
    val zone: ZoneId = ZoneId.systemDefault(),
    val loading: Boolean = false,
    val error: String? = null,
    val ramadan: RamadanTimetable? = null,
    val ramadanLoading: Boolean = false,
    val ramadanError: String? = null,
)

class PrayerViewModel(
    private val calculator: PrayerCalculator,
    private val locationSource: LocationSource,
    private val calculationDispatcher: kotlinx.coroutines.CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Default,
    private val currentZone: () -> ZoneId = { ZoneId.systemDefault() },
    private val currentDate: (ZoneId) -> LocalDate = { LocalDate.now(it) },
    private val settingsStorage: PrayerSettingsStorage? = null,
) : ViewModel() {
    private val initialSettings = try { (settingsStorage?.load() ?: PrayerSettings()) to null }
        catch (_: Exception) { PrayerSettings() to "সংরক্ষিত হিসাবের সেটিংস পড়া যায়নি। পদ্ধতি যাচাই করুন।" }
    private val mutableState = MutableStateFlow(PrayerUiState(settings = initialSettings.first, settingsError = initialSettings.second))
    val state: StateFlow<PrayerUiState> = mutableState.asStateFlow()
    private var calculationJob: kotlinx.coroutines.Job? = null
    private var locationJob: kotlinx.coroutines.Job? = null

    fun refreshLocation() {
        if (mutableState.value.loading) return
        calculationJob?.cancel()
        mutableState.update { it.copy(point = null, qiblaBearing = null, prayers = emptyList(), ramadan = null, ramadanLoading = false, ramadanError = null, loading = true, error = null) }
        locationJob = viewModelScope.launch {
            try {
                val result = locationSource.currentLocation()
                result.fold(
                    onSuccess = { point ->
                        val validPoint = sanitizeGeoPoint(point) ?: error("Invalid coordinates")
                        mutableState.update { it.copy(point = validPoint, loading = false) }
                        recalculate(force = true)
                    },
                    onFailure = { error ->
                        if (error is kotlinx.coroutines.CancellationException) throw error
                        mutableState.update { it.copy(loading = false, error = error.message ?: "অবস্থান পাওয়া যায়নি। আবার চেষ্টা করুন।") }
                    },
                )
            } catch (e: kotlinx.coroutines.CancellationException) { throw e }
            catch (_: Exception) {
                mutableState.update { it.copy(loading = false, error = "অবস্থান পাওয়া যায়নি। আবার চেষ্টা করুন।") }
            }
        }
    }
    fun permissionDenied() {
        locationJob?.cancel()
        calculationJob?.cancel()
        mutableState.update { it.copy(point = null, qiblaBearing = null, prayers = emptyList(), ramadan = null, ramadanLoading = false, ramadanError = null, loading = false, error = "অবস্থানের অনুমতি দেওয়া হয়নি। ফোনের সেটিংস থেকে অনুমতি দিতে পারেন।") }
    }
    private fun updateSettings(settings: PrayerSettings) {
        val error = try { settingsStorage?.save(settings); null }
            catch (_: Exception) { "সেটিংস সংরক্ষণ করা যায়নি; আবার নির্বাচন করুন।" }
        mutableState.update { it.copy(settings = settings, settingsError = error) }
        recalculate(force = true)
    }
    fun setMethod(method: PrayerMethod) = updateSettings(mutableState.value.settings.copy(method = method))
    fun setAsr(asr: AsrMethod) = updateSettings(mutableState.value.settings.copy(asr = asr))

    fun recalculate(force: Boolean = false) {
        val old = mutableState.value
        val zone = currentZone()
        val date = currentDate(zone)
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
                val result = kotlinx.coroutines.withContext(calculationDispatcher) {
                    calculatePrayerData(calculator, point, date, zone, old.settings)
                }
                mutableState.update { it.copy(prayers = result.prayers ?: emptyList(), qiblaBearing = result.qiblaBearing,
                    ramadan = result.ramadan, ramadanLoading = false,
                    error = if (result.prayers == null) "এই অবস্থান ও তারিখের নামাজের সময় পাওয়া যায়নি। স্থানীয় নির্ভরযোগ্য সময়সূচি দেখুন।" else null,
                    ramadanError = if (result.ramadan == null) "রমজানের সময়সূচি পাওয়া যায়নি।" else null) }
            } catch (e: kotlinx.coroutines.CancellationException) { throw e }
            catch (_: Exception) {
                mutableState.update { it.copy(prayers = emptyList(), qiblaBearing = null, ramadan = null, ramadanLoading = false,
                    error = "এই অবস্থানের সময় হিসাব করা যায়নি। আবার চেষ্টা করুন।", ramadanError = "রমজানের সময়সূচি পাওয়া যায়নি।") }
            }
        }
    }
}

class HidayahViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        HomeViewModel::class.java -> HomeViewModel(AppContainer.dailyValueRepository)
        AccountViewModel::class.java -> AccountViewModel(AppContainer.accountRepository)
        QuranViewModel::class.java -> QuranViewModel(AppContainer.quranRepository)
        QuranReaderViewModel::class.java -> QuranReaderViewModel(AppContainer.quranRepository)
        ValuesViewModel::class.java -> ValuesViewModel(AppContainer.quranRepository)
        PrayerViewModel::class.java -> PrayerViewModel(AppContainer.prayerCalculator, DeviceLocationSource(context), settingsStorage = LocalPrayerSettingsStorage(context))
        TrackerViewModel::class.java -> TrackerViewModel(AppContainer.trackerRepository(context))
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    } as T
}
