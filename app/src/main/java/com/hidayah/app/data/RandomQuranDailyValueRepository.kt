package com.hidayah.app.data

import java.time.LocalDate
import kotlin.random.Random
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Picks an online Quran verse for Home without daily admin publishing.
 * A successful selection is reused for the same local day during this app process;
 * it may differ by user or after a process restart. No persistent offline cache is promised.
 * If the Quran source is unavailable, existing Firestore content remains a fallback.
 */
class RandomQuranDailyValueRepository(
    private val quran: QuranRepository,
    private val fallback: DailyValueRepository,
    private val random: Random = Random.Default,
) : DailyValueRepository {
    private val mutex = Mutex()
    private var cachedDay: LocalDate? = null
    private var cachedValue: DailyValue? = null

    override fun observe(day: LocalDate): Flow<DailyValueStatus> = flow {
        emit(DailyValueStatus.Loading)
        val result = try {
            load(day)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            null
        }
        if (result != null) {
            emit(DailyValueStatus.Available(result.first, result.second))
        } else {
            emitAll(fallback.observe(day))
        }
    }

    private suspend fun load(day: LocalDate): Pair<DailyValue, Boolean> = mutex.withLock {
        val previous = cachedValue
        if (cachedDay == day && previous != null) return@withLock previous to true

        val chapters = quran.getSurahs()
        check(chapters.isNotEmpty()) { "No Quran chapters available" }
        val chapter = chapters[random.nextInt(chapters.size)]
        check(chapter.number in 1..114 && chapter.verses in 1..286) { "Invalid chapter metadata" }
        val reference = VerseReference(chapter.number, random.nextInt(1, chapter.verses + 1), "কুরআন")
        val verse = quran.getVerseText(reference)
        check(verse.reference == reference && verse.arabic.isNotBlank() && verse.bangla.isNotBlank()) {
            "Incomplete or mismatched verse"
        }
        val value = DailyValue(
            title = "কুরআন থেকে আজকের আয়াত",
            reflection = "আয়াতটি তার প্রসঙ্গসহ কুরআন পাঠকে পড়ুন।",
            referenceType = "Ayat",
            referenceText = verse.bangla,
            reference = "সূরা ${reference.chapter}, আয়াত ${reference.verse} · বাংলা অনুবাদ: মুহিউদ্দীন খান · Al Quran Cloud",
            referenceArabic = verse.arabic,
        )
        cachedDay = day
        cachedValue = value
        value to false
    }
}
