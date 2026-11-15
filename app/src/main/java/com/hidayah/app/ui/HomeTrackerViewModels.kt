package com.hidayah.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hidayah.app.data.*
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class HomeUiState(val date: LocalDate = LocalDate.now(), val dailyValue: DailyValueStatus = DailyValueStatus.Loading)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(private val repository: DailyValueRepository) : ViewModel() {
    private val date = MutableStateFlow(LocalDate.now())
    private val retry = MutableStateFlow(0)
    val state: StateFlow<HomeUiState> = combine(date, retry) { day, _ -> day }
        .flatMapLatest { day -> flow { emitAll(repository.observe(day)) }.map { HomeUiState(day, it) }
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
    val pendingWrites: Set<String> = emptySet(),
)

@OptIn(ExperimentalCoroutinesApi::class)
class TrackerViewModel(
    private val repository: TrackerRepository,
    private val today: () -> LocalDate = { LocalDate.now() },
) : ViewModel() {
    private val date = MutableStateFlow(today())
    private val retryRead = MutableStateFlow(0)
    private val writeError = MutableStateFlow<String?>(null)
    private val pendingCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val writeMutex = Mutex()
    private val snapshots = combine(date, retryRead) { day, _ -> day }.flatMapLatest { day ->
        flow { emitAll(repository.observeBetween(day.minusDays(6), day)) }
            .map { TrackerUiState(snapshot = trackerSnapshot(day, it), loading = false) }
            .onStart { emit(TrackerUiState(snapshot = trackerSnapshot(day, emptyList()))) }
            .catch { emit(TrackerUiState(snapshot = trackerSnapshot(day, emptyList()), loading = false, error = "Couldn't read local progress.", readFailed = true)) }
    }
    val state = combine(snapshots, writeError, pendingCounts) { snapshot, error, pending ->
        snapshot.copy(error = if (snapshot.readFailed || error == null) snapshot.error else error, pendingWrites = pending.keys)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TrackerUiState())

    fun refreshDate() { date.value = today() }
    fun retryRead() { retryRead.update { it + 1 } }
    fun setCompleted(id: String, completed: Boolean) {
        val today = today()
        val displayed = state.value
        date.value = today
        if (displayed.loading || displayed.readFailed) return
        if (displayed.snapshot.date != today) {
            writeError.value = "দিন পরিবর্তন হয়েছে। নতুন দিনের তালিকা দেখে আবার চিহ্নিত করুন।"
            return
        }
        pendingCounts.update { it + (id to ((it[id] ?: 0) + 1)) }
        viewModelScope.launch {
            try {
                writeMutex.withLock { repository.setCompleted(today, id, completed); writeError.value = null }
            } catch (e: kotlinx.coroutines.CancellationException) { throw e }
            catch (_: Exception) { writeError.value = "পরিবর্তন সংরক্ষণ করা যায়নি। আবার চেষ্টা করুন।" }
            finally {
                pendingCounts.update { counts ->
                    val remaining = (counts[id] ?: 1) - 1
                    if (remaining > 0) counts + (id to remaining) else counts - id
                }
            }
        }
    }
}
