package com.hidayah.app.data

/** Compass degrees clockwise from true north; null means a non-finite reading. */
fun normalizeCompassDegrees(degrees: Double): Double? =
    if (!degrees.isFinite()) null else ((degrees % 360.0) + 360.0) % 360.0

/** Clockwise angle from the device heading to the geographic bearing. */
fun relativeCompassDegrees(bearing: Double, heading: Double): Double? {
    val target = normalizeCompassDegrees(bearing) ?: return null
    val current = normalizeCompassDegrees(heading) ?: return null
    return normalizeCompassDegrees(target - current)
}
