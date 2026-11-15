package com.hidayah.app

import com.hidayah.app.data.CloudQuranRepository
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class CloudTafsirValidationTest {
    private fun response(keys: List<String>, text: String = "<p>text</p>", resource: Int = 165) = JSONObject().put("tafsir",
        JSONObject().put("resource_id", resource).put("language_id", 20).put("resource_name", "Source")
            .put("text", text).put("verses", JSONObject().apply { keys.forEach { put(it, JSONObject()) } }))

    @Test fun acceptsRequestedVerseInMultiVerseTafsir() = runTest {
        val repo = CloudQuranRepository { response(listOf("2:1", "2:2")) }
        val result = repo.getTafsir(2, 2)
        assertTrue("2:2" in result.verseKeys)
        assertEquals("<p>text</p>", result.textHtml)
    }
    @Test fun rejectsMismatchedMissingEmptyOrWrongResource() = runTest {
        for (root in listOf(response(listOf("2:1")), response(emptyList()), response(listOf("2:2"), " "), response(listOf("2:2"), resource = 999))) {
            try {
                CloudQuranRepository { root }.getTafsir(2, 2)
                fail("Invalid tafsir must not be displayed")
            } catch (_: IllegalStateException) { }
        }
    }
}
