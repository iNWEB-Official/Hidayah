package com.hidayah.app.data

/** referenceText is the verified text/translation; Arabic is optional for existing documents. */
data class DailyValue(
    val title: String,
    val reflection: String,
    val referenceType: String,
    val referenceText: String,
    val reference: String,
    val referenceArabic: String? = null,
    val shareBackgroundId: String? = null,
    val shareBackgroundUrl: String? = null,
)

/** Reject incomplete entries rather than inventing religious text or a citation. */
fun parseDailyValue(fields: Map<String, Any?>): DailyValue? {
    fun field(name: String) = (fields[name] as? String)?.trim()?.takeIf { it.isNotEmpty() }
    val type = field("referenceType") ?: return null
    if (type != "Hadith" && type != "Ayat") return null
    return DailyValue(
        title = field("title") ?: return null,
        reflection = field("reflection") ?: return null,
        referenceType = type,
        referenceText = field("referenceText") ?: return null,
        reference = field("reference") ?: return null,
        referenceArabic = field("referenceArabic"),
        shareBackgroundId = field("shareBackgroundId")?.lowercase(),
        shareBackgroundUrl = field("shareBackgroundUrl"),
    )
}

/** Share only published material and its source; never create or infer a quotation. */
fun formatDailyValueShare(value: DailyValue): String = listOfNotNull(
    value.title,
    value.reflection,
    value.referenceArabic?.takeIf { it != value.referenceText },
    value.referenceText,
    "সূত্র: ${value.reference}",
).joinToString("\n\n")

/** Avoid attempting a very large Android intent; do not silently truncate religious content. */
const val MAX_DAILY_SHARE_TEXT_CHARS = 50_000
fun dailyShareTextFitsIntent(text: String): Boolean = text.length <= MAX_DAILY_SHARE_TEXT_CHARS

/** The image contains the same published fields as text sharing, without generating a quotation. */
enum class ShareImageTextStyle { LABEL, TITLE, REFLECTION, ARABIC, TRANSLATION, CITATION }
data class ShareImageTextBlock(val text: String, val style: ShareImageTextStyle)
fun dailyValueImageBlocks(value: DailyValue): List<ShareImageTextBlock> = buildList {
    add(ShareImageTextBlock(if (value.referenceType == "Hadith") "দৈনিক হাদিস" else "দৈনিক আয়াত", ShareImageTextStyle.LABEL))
    add(ShareImageTextBlock(value.title, ShareImageTextStyle.TITLE))
    add(ShareImageTextBlock(value.reflection, ShareImageTextStyle.REFLECTION))
    value.referenceArabic?.takeIf { it.isNotBlank() && it != value.referenceText }?.let { add(ShareImageTextBlock(it, ShareImageTextStyle.ARABIC)) }
    add(ShareImageTextBlock(value.referenceText, ShareImageTextStyle.TRANSLATION))
    add(ShareImageTextBlock("সূত্র: ${value.reference}", ShareImageTextStyle.CITATION))
}
fun fitsShareImageTextLimit(value: DailyValue, limit: Long = 4000L): Boolean =
    dailyValueImageBlocks(value).sumOf { it.text.length.toLong() } <= limit

/** Snapshot null is an error, not proof that a document was never published. */
fun dailyValueFromSnapshot(exists: Boolean?, fromCache: Boolean, fields: Map<String, Any?> = emptyMap()): DailyValueStatus = when (exists) {
    null -> DailyValueStatus.Error("Daily value snapshot unavailable.")
    false -> DailyValueStatus.Missing(fromCache)
    true -> parseDailyValue(fields)?.let { DailyValueStatus.Available(it, fromCache) } ?: DailyValueStatus.Invalid
}

sealed interface DailyValueStatus {
    data object Loading : DailyValueStatus
    data class Available(val value: DailyValue, val fromCache: Boolean, val isFallback: Boolean = false) : DailyValueStatus
    data class Missing(val fromCache: Boolean) : DailyValueStatus
    data object Invalid : DailyValueStatus
    data class Error(val message: String) : DailyValueStatus
}
