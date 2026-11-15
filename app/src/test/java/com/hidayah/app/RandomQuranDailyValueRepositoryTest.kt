package com.hidayah.app

import com.hidayah.app.data.*
import java.time.LocalDate
import kotlin.random.Random
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RandomQuranDailyValueRepositoryTest {
    private val day = LocalDate.of(2026, 11, 15)

    private class FakeQuran : QuranRepository {
        var chapterCalls = 0
        var verseCalls = 0
        var fail = false
        var cancel = false
        var mismatch = false
        var chapters = listOf(Surah(2, "Al-Baqarah", "The Cow", 3))
        var chosen: VerseReference? = null

        override suspend fun getSurahs(): List<Surah> {
            chapterCalls++
            if (cancel) throw CancellationException("cancelled")
            if (fail) error("source unavailable")
            return chapters
        }
        override suspend fun getVerseText(reference: VerseReference): VerseText {
            verseCalls++
            chosen = reference
            val actual = if (mismatch) VerseReference(reference.chapter, reference.verse + 1) else reference
            return VerseText(actual, "نص عربي", "বাংলা অনুবাদ")
        }
        override suspend fun getChapter(number: Int): QuranChapter = error("not used")
        override suspend fun getTafsir(surah: Int, verse: Int): Tafsir = error("not used")
    }

    private class FakeFallback : DailyValueRepository {
        var calls = 0
        override fun observe(day: LocalDate): Flow<DailyValueStatus> = flow {
            calls++
            emit(DailyValueStatus.Missing(false))
        }
    }

    @Test fun validVerseHasSourceAndSameDayReusesInMemorySelection() = runTest {
        val quran = FakeQuran()
        val fallback = FakeFallback()
        val repo = RandomQuranDailyValueRepository(quran, fallback, Random(7))
        val first = repo.observe(day).toList().last() as DailyValueStatus.Available
        val again = repo.observe(day).toList().last() as DailyValueStatus.Available
        assertEquals(first.value, again.value)
        assertFalse(first.fromCache)
        assertTrue(again.fromCache)
        assertEquals(1, quran.chapterCalls)
        assertEquals(1, quran.verseCalls)
        assertEquals(0, fallback.calls)
        assertTrue(quran.chosen!!.verse in 1..3)
        assertEquals("Ayat", first.value.referenceType)
        assertEquals("نص عربي", first.value.referenceArabic)
        assertEquals("বাংলা অনুবাদ", first.value.referenceText)
        assertTrue(first.value.reference.contains("মুহিউদ্দীন খান"))
        assertTrue(first.value.reference.contains("Al Quran Cloud"))
    }

    @Test fun nextDayFetchesANewSelection() = runTest {
        val quran = FakeQuran()
        val repo = RandomQuranDailyValueRepository(quran, FakeFallback(), Random(7))
        repo.observe(day).toList()
        repo.observe(day.plusDays(1)).toList()
        assertEquals(2, quran.chapterCalls)
        assertEquals(2, quran.verseCalls)
    }

    @Test fun sourceFailureUsesExistingFirestoreFallback() = runTest {
        val quran = FakeQuran().apply { fail = true }
        val fallback = FakeFallback()
        val states = RandomQuranDailyValueRepository(quran, fallback, Random(7)).observe(day).toList()
        assertEquals(DailyValueStatus.Missing(false), states.last())
        assertEquals(1, fallback.calls)
    }

    @Test fun mismatchAndInvalidChapterDoNotDisplayUnverifiedVerse() = runTest {
        val quran = FakeQuran().apply { mismatch = true }
        val fallback = FakeFallback()
        val repo = RandomQuranDailyValueRepository(quran, fallback, Random(7))
        assertEquals(DailyValueStatus.Missing(false), repo.observe(day).toList().last())
        quran.mismatch = false
        quran.chapters = listOf(Surah(115, "invalid", "invalid", 1))
        assertEquals(DailyValueStatus.Missing(false), repo.observe(day).toList().last())
        assertEquals(2, fallback.calls)
    }

    @Test fun cancellationIsNotConvertedIntoFallback() = runTest {
        val quran = FakeQuran().apply { cancel = true }
        val fallback = FakeFallback()
        try {
            RandomQuranDailyValueRepository(quran, fallback, Random(7)).observe(day).toList()
            fail("Cancellation must propagate")
        } catch (_: CancellationException) {
            assertEquals(0, fallback.calls)
        }
    }
}
