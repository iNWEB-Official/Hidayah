package com.hidayah.app

import com.hidayah.app.data.CloudQuranRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class CloudQuranChapterParsingTest {
    private fun edition(id: String): JSONObject {
        val ayahs = JSONArray()
        for (i in 1..2) {
            ayahs.put(JSONObject().put("numberInSurah", i).put("number", 100 + i)
                .put("text", "text $i").put("audio", "https://example.com/$i.mp3"))
        }
        return JSONObject().put("edition", JSONObject().put("identifier", id))
            .put("number", 2).put("numberOfAyahs", 2).put("englishName", "Al-Baqarah")
            .put("englishNameTranslation", "The Cow").put("name", "البقرة").put("ayahs", ayahs)
    }
    private fun data(): JSONObject = JSONObject().put("data", JSONArray()
        .put(edition("quran-uthmani")).put(edition("bn.bengali")).put(edition("ar.alafasy")))
    private fun textOnly(): JSONObject = JSONObject().put("data", JSONArray()
        .put(edition("quran-uthmani")).put(edition("bn.bengali")))
    private fun rootEntry(root: JSONObject, index: Int) = root.getJSONArray("data").getJSONObject(index)
    private fun rejected(root: JSONObject) {
        try {
            CloudQuranRepository().parseChapter(2, root)
            fail("Invalid data must not be displayed")
        } catch (_: IllegalStateException) { }
    }

    @Test fun completeMatchingEditionsAreAccepted() {
        val result = CloudQuranRepository().parseChapter(2, data())
        assertEquals(2, result.surah.number)
        assertEquals(listOf(1, 2), result.verses.map { it.number })
        assertEquals("text 1", result.verses[0].arabic)
        assertEquals("text 2", result.verses[1].bangla)
    }

    @Test fun validAudioEditionNeedsNoFallback() = runTest {
        var calls = 0
        val repo = CloudQuranRepository { _ -> calls++; data() }
        val result = repo.getChapter(2)
        assertEquals(1, calls)
        assertEquals(listOf("https://example.com/1.mp3", "https://example.com/2.mp3"), result.verses.map { it.audioUrl })
    }

    @Test fun audioEditionFailureFallsBackToValidatedText() = runTest {
        val requests = mutableListOf<String>()
        val repo = CloudQuranRepository { url ->
            requests.add(url)
            if (url.endsWith("ar.alafasy")) throw IllegalStateException("audio endpoint unavailable")
            textOnly()
        }
        val result = repo.getChapter(2)
        assertEquals(2, requests.size)
        assertTrue(requests.last().endsWith("quran-uthmani,bn.bengali"))
        assertEquals(listOf("text 1", "text 2"), result.verses.map { it.bangla })
        assertTrue(result.verses.all { it.audioUrl == null })
    }

    @Test fun invalidAudioEditionFallsBackToText() = runTest {
        val broken = data()
        rootEntry(broken, 2).getJSONArray("ayahs").getJSONObject(0).put("number", 999)
        var calls = 0
        val repo = CloudQuranRepository { _ -> calls++; if (calls == 1) broken else textOnly() }
        val result = repo.getChapter(2)
        assertEquals(2, calls)
        assertTrue(result.verses.all { it.audioUrl == null })
    }

    @Test fun fallbackDoesNotAllowMismatchedText() = runTest {
        val invalidText = textOnly()
        rootEntry(invalidText, 1).put("number", 3)
        val repo = CloudQuranRepository { url ->
            if (url.endsWith("ar.alafasy")) throw IllegalStateException("audio unavailable")
            invalidText
        }
        try {
            repo.getChapter(2)
            fail("Mismatched text must be rejected")
        } catch (_: IllegalStateException) { }
    }

    @Test fun cancellationDoesNotTriggerFallback() = runTest {
        var calls = 0
        val repo = CloudQuranRepository { _ -> calls++; throw CancellationException("cancelled") }
        try {
            repo.getChapter(2)
            fail("Cancellation must propagate")
        } catch (_: CancellationException) { }
        assertEquals(1, calls)
    }

    @Test fun wrongBanglaSurahIsRejected() {
        val root = data(); rootEntry(root, 1).put("number", 3)
        rejected(root)
    }

    @Test fun wrongGlobalVerseNumberIsRejected() {
        val root = data(); rootEntry(root, 1).getJSONArray("ayahs").getJSONObject(0).put("number", 999)
        rejected(root)
    }

    @Test fun duplicateVerseNumberIsRejected() {
        val root = data(); rootEntry(root, 0).getJSONArray("ayahs").getJSONObject(1).put("numberInSurah", 1)
        rejected(root)
    }

    @Test fun blankTranslationIsRejected() {
        val root = data(); rootEntry(root, 1).getJSONArray("ayahs").getJSONObject(0).put("text", " ")
        rejected(root)
    }

    @Test fun duplicateEditionIsRejected() {
        val root = data(); rootEntry(root, 1).getJSONObject("edition").put("identifier", "quran-uthmani")
        rejected(root)
    }
}
