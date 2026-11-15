package com.hidayah.app.ui

/** The end is exclusive, and indices refer to verses within the loaded chapter. */
internal data class RecitationQueue(val start: Int, val endExclusive: Int)

internal fun recitationQueue(verseCount: Int, start: Int, single: Boolean): RecitationQueue? {
    if (verseCount <= 0 || start !in 0 until verseCount) return null
    return RecitationQueue(start, if (single) start + 1 else verseCount)
}
