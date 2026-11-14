package com.hidayah.app.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/** One-shot, foreground-only location. The app does not persist or directly transmit coordinates. */
class DeviceLocationSource(context: Context) {
    private val appContext = context.applicationContext
    private val manager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    suspend fun currentLocation(): Result<GeoPoint> {
        val fine = ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fine && !coarse) return Result.failure(IllegalStateException("Allow location access to calculate prayer times and Qibla."))
        return try {
            val provider = when {
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                fine && manager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                else -> return Result.failure(IllegalStateException("Turn on device location and try again."))
            }
            val point = withTimeoutOrNull(20_000L) {
                suspendCancellableCoroutine<GeoPoint?> { continuation ->
                    val signal = CancellationSignal()
                    continuation.invokeOnCancellation { signal.cancel() }
                    LocationManagerCompat.getCurrentLocation(manager, provider, signal, ContextCompat.getMainExecutor(appContext)) { location ->
                        if (continuation.isActive) continuation.resume(location?.let {
                            GeoPoint(it.latitude, it.longitude, if (it.hasAltitude()) it.altitude else 0.0,
                                if (it.hasAccuracy()) it.accuracy else null)
                        })
                    }
                }
            }
            if (point == null) Result.failure(IllegalStateException("Couldn't get a location. Check location settings and try again."))
            else Result.success(point)
        } catch (_: SecurityException) {
            Result.failure(IllegalStateException("Location permission was denied or revoked."))
        } catch (_: IllegalArgumentException) {
            Result.failure(IllegalStateException("A location provider is unavailable. Check location settings."))
        }
    }
}
