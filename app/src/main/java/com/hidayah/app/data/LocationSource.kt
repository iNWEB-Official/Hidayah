package com.hidayah.app.data

/** Foreground location request; implementations must propagate coroutine cancellation. */
fun interface LocationSource {
    suspend fun currentLocation(): Result<GeoPoint>
}
