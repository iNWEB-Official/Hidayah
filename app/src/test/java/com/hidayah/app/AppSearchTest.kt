package com.hidayah.app

import com.hidayah.app.data.*
import org.junit.Assert.*
import org.junit.Test

class AppSearchTest {
    @Test fun indexesAllSearchableContentWithUniqueIds() {
        assertEquals(AppSearchIndex.size, AppSearchIndex.map { it.id }.toSet().size)
        assertEquals(ValueTopics.size, AppSearchIndex.count { it.destination == "value" })
        assertEquals(DuaItems.size, AppSearchIndex.count { it.destination == "dua" })
        assertEquals(Stories.size, AppSearchIndex.count { it.destination == "story" })
        assertTrue(AppSearchIndex.all { it.title.isNotBlank() && it.subtitle.isNotBlank() })
    }
    @Test fun emptyQueryShowsSectionsNotAllContent() {
        assertTrue(searchApp("").any { it.destination == "quran" })
        assertTrue(searchApp("  ").any { it.destination == "settings" })
        assertFalse(searchApp("").any { it.destination == "dua" })
    }
    @Test fun findsDuasByReferenceWithBanglaAndEnglishDigits() {
        assertTrue(searchApp("২৩৫৭").any { it.id == "dua-iftar" })
        assertTrue(searchApp("2357").any { it.id == "dua-iftar" })
    }
    @Test fun searchResultsIdentifyCorrectTargets() {
        assertEquals("values/family", searchApp("পারিবারিক মূল্যবোধ").single().let { "values/${it.queryText}" })
        assertEquals("দোয়া ও জিকির", searchApp("দোয়া ও জিকির").first { it.destination == "duas" }.title)
        assertTrue(searchApp("ইউসুফ").any { it.destination == "story" && it.queryText == "ইউসুফ (আ.)-এর ক্ষমা" })
        assertTrue(searchApp("মিলবে না xyz").isEmpty())
    }
}
