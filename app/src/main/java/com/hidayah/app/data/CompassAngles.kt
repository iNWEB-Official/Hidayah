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

/** Do not draw a sensor arrow while it is unavailable, unreliable or invalid. */
enum class CompassDisplay { UNAVAILABLE, WAITING, UNRELIABLE, READY }
fun compassDisplay(available: Boolean, heading: Double?, bearing: Double, accuracy: Int): CompassDisplay = when {
    !available -> CompassDisplay.UNAVAILABLE
    heading == null || relativeCompassDegrees(bearing, heading) == null -> CompassDisplay.WAITING
    // Android's unreliable and no-contact statuses are zero and negative, respectively.
    accuracy <= 0 -> CompassDisplay.UNRELIABLE
    else -> CompassDisplay.READY
}
