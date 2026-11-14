package com.hidayah.app

import com.hidayah.app.data.formatDailyValueShare
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
}
