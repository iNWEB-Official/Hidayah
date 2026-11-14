package com.hidayah.app.data

/** Each reference identifies an actual Quran verse; text comes from the selected editions. */
data class VerseReference(val chapter: Int, val verse: Int, val label: String) {
    val key: String get() = "$chapter:$verse"
}
data class ValueTopic(val id: String, val title: String, val subtitle: String, val detail: String, val source: String, val verses: List<VerseReference>)
val ValueTopics = listOf(
    ValueTopic("personal", "ব্যক্তিগত চরিত্র", "সততা, ধৈর্য ও আত্মসংযম", "সৎ কথা বলা ও ধৈর্যের চর্চা ব্যক্তিগত জীবনের গুরুত্বপূর্ণ গুণ। নিচের আয়াতগুলোতে এ বিষয়ে ভাবার সুযোগ রয়েছে।", "কুরআন ৩৩:৭০; ২:১৫৩", listOf(VerseReference(33, 70, "সঠিক কথা"), VerseReference(2, 153, "ধৈর্য ও প্রার্থনা"))),
    ValueTopic("family", "পারিবারিক মূল্যবোধ", "পিতা-মাতা ও পরিবারের প্রতি দায়িত্ব", "পরিবারে সদ্ব্যবহার ও দায়িত্বশীল আচরণের গুরুত্ব সম্পর্কে নিচের আয়াতগুলো পড়ুন।", "কুরআন ১৭:২৩; ৪:১৯", listOf(VerseReference(17, 23, "পিতা-মাতার প্রতি আচরণ"), VerseReference(4, 19, "সদাচরণ"))),
    ValueTopic("social", "সামাজিক শিষ্টাচার", "মানবিকতা ও পারস্পরিক সম্মান", "মানুষকে সম্মান এবং অন্যদের সঙ্গে সুন্দর আচরণ সম্পর্কে নিচের আয়াতগুলো পড়ুন।", "কুরআন ৪৯:১১; ৪:৩৬", listOf(VerseReference(49, 11, "পারস্পরিক সম্মান"), VerseReference(4, 36, "অন্যের প্রতি সদ্ব্যবহার"))),
    ValueTopic("work", "লেনদেন ও কর্মক্ষেত্র", "ন্যায্যতা ও আমানতদারি", "অর্পিত দায়িত্ব ও পরিমাপে ন্যায্যতা সম্পর্কে কুরআনের নির্দেশনাগুলো পড়ুন।", "কুরআন ৪:৫৮; ৮৩:১–৩", listOf(VerseReference(4, 58, "আমানত ও বিচার"), VerseReference(83, 1, "পরিমাপে ন্যায্যতা — ১"), VerseReference(83, 2, "পরিমাপে ন্যায্যতা — ২"), VerseReference(83, 3, "পরিমাপে ন্যায্যতা — ৩"))),
)

/** Match category text, topic labels and chapter:verse, including Bengali digits. */
fun searchValueTopics(query: String): List<ValueTopic> {
    fun normalize(text: String): String = text.trim().lowercase().map {
        if (it in '০'..'৯') '0' + (it - '০') else it
    }.joinToString("")
    val needle = normalize(query)
    if (needle.isEmpty()) return ValueTopics
    return ValueTopics.filter { topic ->
        val corpus = listOf(topic.title, topic.subtitle, topic.detail, topic.source) + topic.verses.flatMap { listOf(it.label, it.key) }
        corpus.any { normalize(it).contains(needle) }
    }
}
