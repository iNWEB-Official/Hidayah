package com.hidayah.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hidayah.app.data.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuranUiState(val loading: Boolean = false, val surahs: List<Surah> = emptyList(), val error: String? = null)
class QuranViewModel(private val repository: QuranRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(QuranUiState())
    val state: StateFlow<QuranUiState> = mutableState.asStateFlow()
    init { load() }
    fun load() {
        if (mutableState.value.loading) return
        viewModelScope.launch {
            mutableState.update { it.copy(loading = true, error = null) }
            try { val surahs = repository.getSurahs(); mutableState.update { it.copy(loading = false, surahs = surahs) } }
            catch (e: CancellationException) { throw e }
            catch (_: Exception) { mutableState.update { it.copy(loading = false, error = "Could not load surahs. Check your connection and retry.") } }
        }
    }
}

data class TafsirState(val loading: Boolean = false, val data: Tafsir? = null, val error: String? = null, val expanded: Boolean = false)
data class ReaderUiState(val loading: Boolean = false, val chapter: QuranChapter? = null, val error: String? = null, val tafsirs: Map<Int, TafsirState> = emptyMap())
class QuranReaderViewModel(private val repository: QuranRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(ReaderUiState())
    val state: StateFlow<ReaderUiState> = mutableState.asStateFlow()
    private var requestedSurah: Int? = null

    fun load(number: Int, retry: Boolean = false) {
        if (number !in 1..114 || (requestedSurah == number && !retry)) return
        requestedSurah = number
        viewModelScope.launch {
            mutableState.value = ReaderUiState(loading = true)
            try { val chapter = repository.getChapter(number); mutableState.update { it.copy(loading = false, chapter = chapter) } }
            catch (e: CancellationException) { throw e }
            catch (_: Exception) { mutableState.update { it.copy(loading = false, error = "Could not load this surah. Check your connection and retry.") } }
        }
    }

    fun toggleTafsir(verse: Int) {
        val number = requestedSurah ?: return
        val previous = mutableState.value.tafsirs[verse] ?: TafsirState()
        if (previous.expanded) {
            setTafsir(verse, previous.copy(expanded = false)); return
        }
        if (previous.data != null || previous.loading) {
            setTafsir(verse, previous.copy(expanded = true)); return
        }
        setTafsir(verse, TafsirState(loading = true, expanded = true))
        viewModelScope.launch {
            try {
                val result = repository.getTafsir(number, verse)
                val latest = mutableState.value.tafsirs[verse] ?: return@launch
                setTafsir(verse, latest.copy(loading = false, data = result, error = null))
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) {
                val latest = mutableState.value.tafsirs[verse] ?: return@launch
                setTafsir(verse, latest.copy(loading = false, error = "Tafsir unavailable. Tap to retry."))
            }
        }
    }
    private fun setTafsir(verse: Int, value: TafsirState) { mutableState.update { it.copy(tafsirs = it.tafsirs + (verse to value)) } }
}
