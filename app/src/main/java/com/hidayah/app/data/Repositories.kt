package com.hidayah.app.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface QuranRepository {
    suspend fun getSurahs(): List<Surah>
    suspend fun getChapter(number: Int): QuranChapter
    suspend fun getVerseText(reference: VerseReference): VerseText
    suspend fun getTafsir(surah: Int, verse: Int): Tafsir
}
interface TrackerRepository {
    fun observeBetween(first: LocalDate, last: LocalDate): Flow<List<DeedRecord>>
    suspend fun setCompleted(day: LocalDate, id: String, completed: Boolean)
}
interface DailyValueRepository { fun observe(day: LocalDate): Flow<DailyValueStatus> }

/** Manual app-scoped dependencies; tracker database is created once using application context. */
object AppContainer {
    val quranRepository: QuranRepository = CloudQuranRepository()
    val prayerCalculator: PrayerCalculator = AdhanPrayerCalculator()
    val dailyValueRepository: DailyValueRepository = FirestoreDailyValueRepository()
    @Volatile private var tracker: TrackerRepository? = null
    fun trackerRepository(context: Context): TrackerRepository = tracker ?: synchronized(this) {
        tracker ?: RoomTrackerRepository(HidayahDatabase.getInstance(context).deedDao()).also { tracker = it }
    }
}
