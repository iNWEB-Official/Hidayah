package com.hidayah.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hidayah.app.data.QuranRepository
import com.hidayah.app.data.VerseReference
import com.hidayah.app.data.VerseText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface VerseLoadState {
    data object Loading : VerseLoadState
    data class Loaded(val verse: VerseText) : VerseLoadState
    data object Error : VerseLoadState
}

/** A separate result for each verse avoids hiding successful verses if one request fails. */
class ValuesViewModel(private val repository: QuranRepository) : ViewModel() {
    private val mutableVerses = MutableStateFlow<Map<String, VerseLoadState>>(emptyMap())
    val verses: StateFlow<Map<String, VerseLoadState>> = mutableVerses.asStateFlow()

    fun load(reference: VerseReference, retry: Boolean = false) {
        val old = mutableVerses.value[reference.key]
        if (old is VerseLoadState.Loading || old is VerseLoadState.Loaded || (old is VerseLoadState.Error && !retry)) return
        mutableVerses.update { it + (reference.key to VerseLoadState.Loading) }
        viewModelScope.launch {
            try {
                val verse = repository.getVerseText(reference)
                mutableVerses.update { it + (reference.key to VerseLoadState.Loaded(verse)) }
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) { mutableVerses.update { it + (reference.key to VerseLoadState.Error) } }
        }
    }
}
