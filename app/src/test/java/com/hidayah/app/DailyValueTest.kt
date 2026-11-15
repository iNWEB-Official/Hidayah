package com.hidayah.app

import com.hidayah.app.data.formatDailyValueShare
import com.hidayah.app.data.dailyShareTextFitsIntent
import com.hidayah.app.data.MAX_DAILY_SHARE_TEXT_CHARS
import com.hidayah.app.data.dailyValueImageBlocks
import com.hidayah.app.data.fitsShareImageTextLimit
import com.hidayah.app.data.ShareImageTextStyle
import com.hidayah.app.data.DailyValueStatus
import com.hidayah.app.data.dailyValueFromSnapshot
import com.hidayah.app.data.parseDailyValue
import org.junit.Assert.*
import org.junit.Test

class DailyValueTest {
    private val base: Map<String, Any?> = mapOf(
        "title" to "  মূল্যবোধ  ", "reflection" to "ব্যাখ্যা", "referenceType" to " Hadith ",
        "referenceText" to "বাংলা অর্থ", "reference" to "সূত্র",
    )

    @Test fun existingDocumentsStillWorkWithoutArabic() {
        val value = parseDailyValue(base)!!
        assertEquals("মূল্যবোধ", value.title)
        assertEquals("Hadith", value.referenceType)
        assertNull(value.referenceArabic)
    }

    @Test fun optionalArabicIsTrimmedAndIncludedWhenSharing() {
        val value = parseDailyValue(base + ("referenceArabic" to "  نص عربي  "))!!
        assertEquals("نص عربي", value.referenceArabic)
        val shared = formatDailyValueShare(value)
        assertTrue(shared.contains("ব্যাখ্যা"))
        assertTrue(shared.contains("نص عربي"))
        assertTrue(shared.contains("বাংলা অর্থ"))
        assertTrue(shared.contains("সূত্র: সূত্র"))
    }

    @Test fun emptyOptionalArabicIsNotShown() {
        assertNull(parseDailyValue(base + ("referenceArabic" to "   "))!!.referenceArabic)
    }

    @Test fun missingCitationOrUnsupportedTypeIsRejected() {
        assertNull(parseDailyValue(base - "reference"))
        assertNull(parseDailyValue(base + ("referenceType" to "Other")))
        assertNull(parseDailyValue(base + ("referenceText" to "  ")))
    }

    @Test fun eachRequiredFieldMustBeANonBlankString() {
        val fields = listOf("title", "reflection", "referenceType", "referenceText", "reference")
        for (field in fields) {
            assertNull("missing $field", parseDailyValue(base - field))
            assertNull("blank $field", parseDailyValue(base + (field to "  ")))
            assertNull("wrong type $field", parseDailyValue(base + (field to 123)))
        }
    }

    @Test fun ayatSharePreservesPublishedContentAndCitation() {
        val value = parseDailyValue(base + ("referenceType" to "Ayat"))!!
        assertEquals("Ayat", value.referenceType)
        assertEquals("মূল্যবোধ\n\nব্যাখ্যা\n\nবাংলা অর্থ\n\nসূত্র: সূত্র", formatDailyValueShare(value))
    }

    @Test fun duplicateArabicAndReferenceTextAreSharedOnlyOnce() {
        val value = parseDailyValue(base + mapOf("referenceText" to "একই লেখা", "referenceArabic" to " একই লেখা "))!!
        assertEquals(1, Regex("একই লেখা").findAll(formatDailyValueShare(value)).count())
        assertTrue(formatDailyValueShare(value).contains("সূত্র: সূত্র"))
    }

    @Test fun malformedOptionalFieldsDoNotBreakPublishedDocuments() {
        val value = parseDailyValue(base + mapOf("referenceArabic" to 123, "shareBackgroundId" to false, "shareBackgroundUrl" to " "))!!
        assertNull(value.referenceArabic)
        assertNull(value.shareBackgroundId)
        assertNull(value.shareBackgroundUrl)
    }

    @Test fun snapshotStatesDoNotConfuseMissingCacheInvalidAndUnavailable() {
        assertTrue(dailyValueFromSnapshot(null, false) is DailyValueStatus.Error)
        assertEquals(DailyValueStatus.Missing(true), dailyValueFromSnapshot(false, true))
        assertEquals(DailyValueStatus.Missing(false), dailyValueFromSnapshot(false, false))
        assertEquals(DailyValueStatus.Invalid, dailyValueFromSnapshot(true, false, base - "reference"))
        val available = dailyValueFromSnapshot(true, true, base) as DailyValueStatus.Available
        assertTrue(available.fromCache)
        assertEquals("সূত্র", available.value.reference)
    }
    @Test fun imageBlocksContainPublishedReflectionArabicTranslationAndCitation() {
        val value = parseDailyValue(base + ("referenceArabic" to "نص عربي"))!!
        val blocks = dailyValueImageBlocks(value)
        assertEquals(listOf(ShareImageTextStyle.LABEL, ShareImageTextStyle.TITLE, ShareImageTextStyle.REFLECTION,
            ShareImageTextStyle.ARABIC, ShareImageTextStyle.TRANSLATION, ShareImageTextStyle.CITATION), blocks.map { it.style })
        assertEquals("ব্যাখ্যা", blocks.single { it.style == ShareImageTextStyle.REFLECTION }.text)
        assertEquals("نص عربي", blocks.single { it.style == ShareImageTextStyle.ARABIC }.text)
        assertEquals("বাংলা অর্থ", blocks.single { it.style == ShareImageTextStyle.TRANSLATION }.text)
        assertEquals("সূত্র: সূত্র", blocks.last().text)
    }
    @Test fun imageLimitIncludesReflectionAndDoesNotTruncateTextFallback() {
        val value = parseDailyValue(base + ("reflection" to "দ".repeat(4100)))!!
        assertFalse(fitsShareImageTextLimit(value))
        assertTrue(formatDailyValueShare(value).contains(value.reflection))
        assertTrue(formatDailyValueShare(value).contains("সূত্র: সূত্র"))
    }
    @Test fun longTextIntentIsRejectedWithoutTruncatingFormattedContent() {
        assertTrue(dailyShareTextFitsIntent("x".repeat(MAX_DAILY_SHARE_TEXT_CHARS)))
        assertFalse(dailyShareTextFitsIntent("x".repeat(MAX_DAILY_SHARE_TEXT_CHARS + 1)))
        val value = parseDailyValue(base + ("reflection" to "দ".repeat(MAX_DAILY_SHARE_TEXT_CHARS)))!!
        val text = formatDailyValueShare(value)
        assertFalse(dailyShareTextFitsIntent(text))
        assertTrue(text.contains(value.reflection))
        assertTrue(text.endsWith("সূত্র: সূত্র"))
    }
    @Test fun optionalBackgroundFieldsAreTrimmedWithoutChangingContent() {
        val value = parseDailyValue(base + mapOf("shareBackgroundId" to "  CHARITY_B ", "shareBackgroundUrl" to " https://example.com/bg.jpg "))!!
        assertEquals("charity_b", value.shareBackgroundId)
        assertEquals("https://example.com/bg.jpg", value.shareBackgroundUrl)
        assertEquals("বাংলা অর্থ", value.referenceText)
        assertEquals("সূত্র", value.reference)
    }

}
