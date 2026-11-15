package com.hidayah.app.data

/** Reject unusable coordinates; do not pretend invalid accuracy is a precise reading. */
fun sanitizeGeoPoint(point: GeoPoint): GeoPoint? {
    if (!point.latitude.isFinite() || point.latitude !in -90.0..90.0 ||
        !point.longitude.isFinite() || point.longitude !in -180.0..180.0) return null
    val altitude = point.altitudeMeters.takeIf { it.isFinite() } ?: 0.0
    val accuracy = point.accuracyMeters?.takeIf { it.isFinite() && it >= 0f }
    return point.copy(altitudeMeters = altitude, accuracyMeters = accuracy)
}

enum class LocationAccuracy { UNKNOWN, APPROXIMATE, VERY_APPROXIMATE }
fun locationAccuracy(point: GeoPoint): LocationAccuracy = when (val meters = point.accuracyMeters) {
    null -> LocationAccuracy.UNKNOWN
    else -> if (!meters.isFinite() || meters < 0f) LocationAccuracy.UNKNOWN
        else if (meters >= 1000f) LocationAccuracy.VERY_APPROXIMATE else LocationAccuracy.APPROXIMATE
}
