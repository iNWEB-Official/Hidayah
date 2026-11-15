package com.hidayah.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hidayah.app.data.*
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(val date: LocalDate = LocalDate.now(), val dailyValue: DailyValueStatus = DailyValueStatus.Loading)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(private val repository: DailyValueRepository) : ViewModel() {
    private val date = MutableStateFlow(LocalDate.now())
    private val retry = MutableStateFlow(0)
    val state: StateFlow<HomeUiState> = combine(date, retry) { day, _ -> day }
        .flatMapLatest { day -> repository.observe(day).map { HomeUiState(day, it) }
            .onStart { emit(HomeUiState(day, DailyValueStatus.Loading)) }
            .catch { emit(HomeUiState(day, DailyValueStatus.Error("Unable to load today's value."))) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
    fun refreshDate() { date.value = LocalDate.now() }
    fun retry() { retry.update { it + 1 } }
}

data class TrackerUiState(
    val snapshot: TrackerSnapshot = trackerSnapshot(LocalDate.now(), emptyList()),
    val loading: Boolean = true,
    val error: String? = null,
    val readFailed: Boolean = false,
)

@OptIn(ExperimentalCoroutinesApi::class)
class TrackerViewModel(
    private val repository: TrackerRepository,
    private val today: () -> LocalDate = { LocalDate.now() },
) : ViewModel() {
    private val date = MutableStateFlow(today())
    private val retryRead = MutableStateFlow(0)
    private val writeError = MutableStateFlow<String?>(null)
    private val snapshots = combine(date, retryRead) { day, _ -> day }.flatMapLatest { day ->
        flow { emitAll(repository.observeBetween(day.minusDays(6), day)) }
            .map { TrackerUiState(snapshot = trackerSnapshot(day, it), loading = false) }
            .onStart { emit(TrackerUiState(snapshot = trackerSnapshot(day, emptyList()))) }
            .catch { emit(TrackerUiState(snapshot = trackerSnapshot(day, emptyList()), loading = false, error = "Couldn't read local progress.", readFailed = true)) }
    }
    val state = combine(snapshots, writeError) { snapshot, error -> if (snapshot.readFailed || error == null) snapshot else snapshot.copy(error = error) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TrackerUiState())

    fun refreshDate() { date.value = today() }
    fun retryRead() { retryRead.update { it + 1 } }
    fun setCompleted(id: String, completed: Boolean) {
        val today = today()
        date.value = today
        viewModelScope.launch {
            try { repository.setCompleted(today, id, completed); writeError.value = null }
            catch (e: kotlinx.coroutines.CancellationException) { throw e }
            catch (_: Exception) { writeError.value = "Couldn't save progress on this device." }
        }
    }
}
