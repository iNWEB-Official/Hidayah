package com.hidayah.app.data

/** referenceText is the verified text/translation; Arabic is optional for existing documents. */
data class DailyValue(
    val title: String,
    val reflection: String,
    val referenceType: String,
    val referenceText: String,
    val reference: String,
    val referenceArabic: String? = null,
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

sealed interface DailyValueStatus {
    data object Loading : DailyValueStatus
    data class Available(val value: DailyValue, val fromCache: Boolean) : DailyValueStatus
    data class Missing(val fromCache: Boolean) : DailyValueStatus
    data object Invalid : DailyValueStatus
    data class Error(val message: String) : DailyValueStatus
}
