package com.hidayah.app.data

import java.time.LocalDate

val DeedDefinitions = listOf(
    Habit("fajr", "Fajr", "Dawn prayer"),
    Habit("dhuhr", "Dhuhr", "Midday prayer"),
    Habit("asr", "Asr", "Afternoon prayer"),
    Habit("maghrib", "Maghrib", "Sunset prayer"),
    Habit("isha", "Isha", "Night prayer"),
    Habit("quran", "Read Quran", "Spend a moment with the Quran"),
    Habit("good_deed", "Good deed", "Make a positive difference"),
)
data class HistoryDay(val date: LocalDate, val completed: Int, val total: Int)
data class TrackerSnapshot(val date: LocalDate, val habits: List<Habit>, val history: List<HistoryDay>)

fun trackerSnapshot(today: LocalDate, records: List<DeedRecord>): TrackerSnapshot {
    val validIds = DeedDefinitions.map { it.id }.toSet()
    val doneByDay = records.asSequence().filter { it.completed && it.deedId in validIds }
        .groupBy({ it.day }, { it.deedId }).mapValues { it.value.toSet() }
    val todayDone = doneByDay[today.toString()].orEmpty()
    val habits = DeedDefinitions.map { it.copy(completed = it.id in todayDone) }
    val history = (0L..6L).map { offset ->
        val day = today.minusDays(offset)
        HistoryDay(day, doneByDay[day.toString()].orEmpty().size, DeedDefinitions.size)
    }
    return TrackerSnapshot(today, habits, history)
}
