package com.hidayah.app

import com.hidayah.app.data.DuaCategories
import com.hidayah.app.data.DuaItems
import com.hidayah.app.data.formatDuaShare
import com.hidayah.app.data.searchDuas
import org.junit.Assert.*
import org.junit.Test

class DuaContentTest {
    @Test fun contentHasUniqueIdsCategoriesAndSources() {
        assertEquals(9, DuaItems.size)
        assertEquals(DuaItems.size, DuaItems.map { it.id }.toSet().size)
        assertTrue(DuaItems.all { it.category in DuaCategories && it.arabic.isNotBlank() && it.pronunciation.isNotBlank() && it.meaning.isNotBlank() && it.source.isNotBlank() && it.sourceUrl.startsWith("https://") })
    }
    @Test fun filtersByCategoryAndFavoritesWithoutNetwork() {
        assertEquals(setOf("wakeup", "iftar"), searchDuas("", "পছন্দ", setOf("wakeup", "iftar")).map { it.id }.toSet())
        assertTrue(searchDuas("", "পছন্দ", emptySet()).isEmpty())
        assertEquals("iftar", searchDuas("", "রমজান", emptySet()).single().id)
    }
    @Test fun searchesBengaliTextAndNumbers() {
        assertEquals("wakeup", searchDuas("ঘুম থেকে", "সব", emptySet()).single().id)
        assertEquals("iftar", searchDuas("2357", "সব", emptySet()).single().id)
        assertEquals("iftar", searchDuas("২৩৫৭", "সব", emptySet()).single().id)
        assertTrue(searchDuas("অজানা-দোয়া", "সব", emptySet()).isEmpty())
    }
    @Test fun shareIncludesArabicMeaningAndReference() {
        val dua = DuaItems.first()
        val share = formatDuaShare(dua)
        assertTrue(share.contains(dua.arabic))
        assertTrue(share.contains(dua.pronunciation))
        assertTrue(share.contains(dua.meaning))
        assertTrue(share.contains(dua.sourceUrl))
    }
}
