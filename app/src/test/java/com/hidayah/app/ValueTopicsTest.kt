package com.hidayah.app

import com.hidayah.app.data.ValueTopics
import com.hidayah.app.data.searchValueTopics
import org.junit.Assert.*
import org.junit.Test

class ValueTopicsTest {
    @Test fun topicsHaveUniqueRoutesAndValidReferences() {
        assertEquals(4, ValueTopics.size)
        assertEquals(ValueTopics.size, ValueTopics.map { it.id }.toSet().size)
        ValueTopics.forEach { topic ->
            assertTrue(topic.verses.isNotEmpty())
            assertEquals(topic.verses.size, topic.verses.map { it.key }.toSet().size)
            assertTrue(topic.verses.all { it.chapter in 1..114 && it.verse in 1..286 && it.label.isNotBlank() })
        }
    }

    @Test fun searchMatchesBengaliTextAndEitherDigitStyle() {
        assertEquals("personal", searchValueTopics("ধৈর্য").single().id)
        assertEquals("work", searchValueTopics("৪:৫৮").single().id)
        assertEquals("work", searchValueTopics("4:58").single().id)
        assertEquals(4, searchValueTopics("  ").size)
        assertTrue(searchValueTopics("অজানা-বিষয়").isEmpty())
    }
}
