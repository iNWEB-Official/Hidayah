package com.hidayah.app.data

data class Surah(val number: Int, val name: String, val meaning: String, val verses: Int, val arabicName: String = "")
data class VerseText(val reference: VerseReference, val arabic: String, val bangla: String)
data class QuranVerse(val number: Int, val arabic: String, val bangla: String, val audioUrl: String?)
data class QuranChapter(val surah: Surah, val verses: List<QuranVerse>)
data class Tafsir(val textHtml: String, val resourceName: String, val verseKeys: List<String>)
data class Prayer(val name: String, val time: String? = null)
data class Habit(val id: String, val title: String, val description: String, val completed: Boolean = false)
