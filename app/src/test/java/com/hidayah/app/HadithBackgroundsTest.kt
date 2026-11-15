package com.hidayah.app

import com.hidayah.app.data.*
import org.junit.Assert.*
import org.junit.Test

class HadithBackgroundsTest {
    private val base = DailyValue("পরীক্ষার বিষয়", "ব্যাখ্যা", "Hadith", "প্রকাশিত লেখা", "প্রকাশিত সূত্র")

    @Test fun exactlyTwentyUniqueLocalBackgroundIds() {
        val ids = HadithBackgroundGroups.flatMap { it.ids }
        assertEquals(20, ids.size)
        assertEquals(20, ids.toSet().size)
        assertTrue(ids.all { it.matches(Regex("[a-z]+_[ab]")) })
    }
    @Test fun explicitAdminIdWinsOverKeywords() {
        val value = base.copy(title = "ধৈর্য", shareBackgroundId = "charity_b")
        assertEquals(BackgroundSelection("charity_b", true), selectHadithBackground(value))
    }
    @Test fun keywordsSelectMatchingThemeAndRemainStable() {
        val value = base.copy(title = "ধৈর্য নিয়ে চিন্তা")
        val selection = selectHadithBackground(value)
        assertTrue(selection.id in listOf("patience_a", "patience_b"))
        assertFalse(selection.selectedByAdmin)
        assertEquals(selection, selectHadithBackground(value))
    }
    @Test fun unicodeEquivalentKeywordStillUsesMatchingGroup() {
        val decomposed = java.text.Normalizer.normalize("ধৈর্য", java.text.Normalizer.Form.NFD)
        assertTrue(selectHadithBackground(base.copy(title = decomposed)).id in listOf("patience_a", "patience_b"))
    }
    @Test fun unknownIdAndUnmatchedTextUseGeneralBackground() {
        val selection = selectHadithBackground(base.copy(shareBackgroundId = "no_such_image"))
        assertTrue(selection.id in listOf("general_a", "general_b"))
        assertFalse(selection.selectedByAdmin)
    }
    @Test fun acceptsOnlyDirectHttpsDnsUrlsWithoutCredentials() {
        assertEquals("https://example.com/image.jpg", safeShareBackgroundUrl(" https://example.com/image.jpg "))
        assertNull(safeShareBackgroundUrl("http://example.com/a.jpg"))
        assertNull(safeShareBackgroundUrl("file:///private/a.png"))
        assertNull(safeShareBackgroundUrl("https://user:pass@example.com/a.jpg"))
        assertNull(safeShareBackgroundUrl("https://127.0.0.1/a.jpg"))
        assertNull(safeShareBackgroundUrl("https://localhost/a.jpg"))
        assertNull(safeShareBackgroundUrl("https://localhost./a.jpg"))
        assertNull(safeShareBackgroundUrl("https://metadata.google.internal./a.jpg"))
        assertNull(safeShareBackgroundUrl("https://example..com/a.jpg"))
        assertNull(safeShareBackgroundUrl("https://example.com:8443/a.jpg"))
        assertNull(safeShareBackgroundUrl("https://example.com/a.jpg#fragment"))
        assertNull(safeShareBackgroundUrl(null))
    }
    @Test fun optionalFieldsDoNotBreakExistingPublishedDocuments() {
        val fields = mapOf("title" to "মূল্যবোধ", "reflection" to "ব্যাখ্যা", "referenceType" to "Hadith", "referenceText" to "প্রকাশিত লেখা", "reference" to "সূত্র")
        val old = parseDailyValue(fields)!!
        assertNull(old.shareBackgroundId)
        assertNull(old.shareBackgroundUrl)
        val updated = parseDailyValue(fields + mapOf("shareBackgroundId" to "  CHARITY_A ", "shareBackgroundUrl" to " https://example.com/a.jpg "))!!
        assertEquals("charity_a", updated.shareBackgroundId)
        assertEquals("https://example.com/a.jpg", updated.shareBackgroundUrl)
        assertTrue(formatDailyValueShare(updated).contains("সূত্র: সূত্র"))
    }
}
