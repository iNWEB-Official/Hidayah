package com.hidayah.app.data

import java.net.URI
import java.util.Locale

data class BackgroundGroup(val label: String, val ids: List<String>, val keywords: List<String>)

/** 20 local backgrounds: 19 original AI scenes and one variant; keyword selection is not AI interpretation. */
val HadithBackgroundGroups = listOf(
    BackgroundGroup("ধৈর্য", listOf("patience_a", "patience_b"), listOf("ধৈর্য", "সবর", "sabr", "patience")),
    BackgroundGroup("দয়া", listOf("compassion_a", "compassion_b"), listOf("দয়া", "করুণা", "রহমত", "মমতা", "mercy", "compassion")),
    BackgroundGroup("দান", listOf("charity_a", "charity_b"), listOf("দান", "সদকা", "সাদাকা", "যাকাত", "charity")),
    BackgroundGroup("ইবাদত", listOf("prayer_a", "prayer_b"), listOf("নামাজ", "সালাত", "সালাহ", "প্রার্থনা", "ইবাদত", "prayer")),
    BackgroundGroup("সততা", listOf("honesty_a", "honesty_b"), listOf("সততা", "সত্য", "আমানত", "ন্যায়", "honesty", "truth")),
    BackgroundGroup("জ্ঞান", listOf("knowledge_a", "knowledge_b"), listOf("জ্ঞান", "ইলম", "শিক্ষা", "knowledge")),
    BackgroundGroup("পরিবার", listOf("family_a", "family_b"), listOf("পিতা", "মাতা", "বাবা", "মা-বাবা", "পরিবার", "সন্তান", "family")),
    BackgroundGroup("কৃতজ্ঞতা", listOf("gratitude_a", "gratitude_b"), listOf("কৃতজ্ঞ", "শুকর", "শোকর", "gratitude")),
    BackgroundGroup("প্রতিবেশী", listOf("neighbor_a", "neighbor_b"), listOf("প্রতিবেশী", "প্রতিবেশ", "neighbor", "neighbour")),
    BackgroundGroup("সাধারণ", listOf("general_a", "general_b"), emptyList()),
)
val HadithBackgroundIds = HadithBackgroundGroups.flatMap { it.ids }.toSet()

data class BackgroundSelection(val id: String, val selectedByAdmin: Boolean)

fun selectHadithBackground(value: DailyValue): BackgroundSelection {
    val requested = value.shareBackgroundId?.trim()?.lowercase(Locale.ROOT)
    if (requested != null && requested in HadithBackgroundIds) return BackgroundSelection(requested, true)
    val text = listOf(value.title, value.reflection, value.referenceText).joinToString(" ").lowercase(Locale.ROOT)
    val group = HadithBackgroundGroups.firstOrNull { g -> g.keywords.any { text.contains(it) } } ?: HadithBackgroundGroups.last()
    val index = (value.title + value.reference).hashCode().and(Int.MAX_VALUE) % group.ids.size
    return BackgroundSelection(group.ids[index], false)
}

/** Syntax filtering only; a DNS hostname can still resolve to a private address. Use trusted hosts. */
fun safeShareBackgroundUrl(raw: String?): String? {
    val value = raw?.trim()?.takeIf { it.length in 1..2048 } ?: return null
    return try {
        val uri = URI(value)
        val host = uri.host?.lowercase(Locale.ROOT) ?: return null
        if (uri.scheme?.lowercase(Locale.ROOT) != "https" || uri.userInfo != null || uri.port !in listOf(-1, 443) ||
            host == "localhost" || host.endsWith(".localhost") || host.endsWith(".local") || host.endsWith(".internal") ||
            !host.contains('.') || host.all { it.isDigit() || it == '.' } || ':' in host || '\\' in value || uri.fragment != null) null else value
    } catch (_: Exception) { null }
}
