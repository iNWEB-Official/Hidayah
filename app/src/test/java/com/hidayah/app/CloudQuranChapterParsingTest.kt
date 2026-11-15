package com.hidayah.app

import com.hidayah.app.data.CloudQuranRepository
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
