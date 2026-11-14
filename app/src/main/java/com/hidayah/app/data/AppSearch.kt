package com.hidayah.app.data

/** Offline index of destinations and titles; this is not a full-text Quran search. */
data class AppSearchResult(val id: String, val title: String, val subtitle: String, val destination: String, val queryText: String = "", val searchText: String = "")

val AppSearchIndex: List<AppSearchResult> = listOf(
    AppSearchResult("home", "হোম", "মূল পাতা", "home"),
    AppSearchResult("quran", "আল-কুরআন", "সূরার তালিকা ও তিলাওয়াত", "quran", searchText = "কুরআন সূরা quran"),
    AppSearchResult("prayer", "নামাজের সময় ও রমজান", "নামাজ, কিবলা ও রমজানের সময়সূচি", "prayer", searchText = "নামাজ কিবলা ইফতার সেহরি রমজান prayer"),
    AppSearchResult("tracker", "আমল ট্র্যাকার", "দৈনিক আমল ও সাপ্তাহিক অগ্রগতি", "tracker"),
    AppSearchResult("values", "বিষয়ভিত্তিক মূল্যবোধ", "কুরআনের আয়াত ও মূল্যবোধ", "values"),
    AppSearchResult("duas", "দোয়া ও জিকির", "দৈনন্দিন দোয়া ও জিকিরের তালিকা", "duas"),
    AppSearchResult("learning", "শিক্ষণীয় গল্প ও কুইজ", "গল্প পড়ুন ও কুইজ খেলুন", "learning"),
    AppSearchResult("settings", "সেটিংস", "থিম, ফন্ট ও স্মরণবার্তা", "settings"),
) + ValueTopics.map { AppSearchResult("value-${it.id}", it.title, "মূল্যবোধ · ${it.subtitle}", "value", it.id, it.source + " " + it.detail + " " + it.verses.joinToString(" ") { v -> v.key }) } +
    DuaItems.map { AppSearchResult("dua-${it.id}", it.title, "দোয়া ও জিকির · ${it.source}", "dua", it.title, it.source + " " + it.category + " " + it.meaning + " " + it.arabic) } +
    Stories.map { AppSearchResult("story-${it.id}", it.title, "গল্প · ${it.source}", "story", it.title, it.source + " " + it.category + " " + it.summary) }

private fun normalizeAppSearch(text: String): String = text.trim().lowercase().map {
    if (it in '০'..'৯') '0' + (it - '০') else it
}.joinToString("")
fun searchApp(query: String): List<AppSearchResult> {
    val needle = normalizeAppSearch(query)
    if (needle.isEmpty()) return AppSearchIndex.filter { it.id in setOf("quran", "prayer", "tracker", "values", "duas", "learning", "settings") }
    return AppSearchIndex.filter { item -> listOf(item.title, item.subtitle, item.searchText).any { normalizeAppSearch(it).contains(needle) } }
}
