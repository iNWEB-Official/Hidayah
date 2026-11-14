package com.hidayah.app.ui

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.hidayah.app.data.DuaItem
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DuaSpeechState(val ready: Boolean = false, val playingId: String? = null, val message: String? = null)

/** Device text-to-speech, not a recorded recitation. Released when the screen is disposed. */
class DuaSpeech(context: Context) {
    private val mutable = MutableStateFlow(DuaSpeechState())
    val state: StateFlow<DuaSpeechState> = mutable.asStateFlow()
    @Volatile private var closed = false
    private var engine: TextToSpeech? = null
    @Volatile private var currentToken: String? = null
    private var sequence = 0L

    init {
        engine = TextToSpeech(context.applicationContext) { status ->
            if (!closed) mutable.update { it.copy(ready = status == TextToSpeech.SUCCESS,
                message = if (status == TextToSpeech.SUCCESS) null else "ডিভাইসের পাঠক চালু করা যায়নি।") }
        }
        engine?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit
            override fun onDone(utteranceId: String?) {
                if (!closed && currentToken == utteranceId) mutable.update { it.copy(playingId = null) }
            }
            @Deprecated("Android callback")
            override fun onError(utteranceId: String?) {
                if (!closed && currentToken == utteranceId) mutable.update { it.copy(playingId = null, message = "এই দোয়াটি পড়া যায়নি।") }
            }
        })
    }

    fun play(dua: DuaItem) {
        val tts = engine
        if (tts == null || !mutable.value.ready) { mutable.update { it.copy(message = "ডিভাইসের পাঠক প্রস্তুত নয়।") }; return }
        val language = Locale("ar")
        if (!runCatching { tts.isLanguageAvailable(language) >= 0 && tts.setLanguage(language) >= 0 }.getOrDefault(false)) {
            mutable.update { it.copy(message = "ডিভাইসে আরবি পাঠের কণ্ঠ নেই। সিস্টেম সেটিংসে কণ্ঠ ইনস্টল করুন।") }; return
        }
        val token = "${dua.id}-${++sequence}"
        currentToken = token
        mutable.update { it.copy(playingId = dua.id, message = null) }
        val result = runCatching { tts.speak(dua.arabic, TextToSpeech.QUEUE_FLUSH, null, token) }.getOrDefault(TextToSpeech.ERROR)
        if (result == TextToSpeech.ERROR) mutable.update { it.copy(playingId = null, message = "এই দোয়াটি পড়া যায়নি।") }
    }
    fun stop() { currentToken = null; engine?.stop(); mutable.update { it.copy(playingId = null) } }
    fun close() { closed = true; engine?.stop(); engine?.shutdown(); engine = null }
}
