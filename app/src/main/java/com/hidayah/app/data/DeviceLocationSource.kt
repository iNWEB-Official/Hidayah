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
class DeviceLocationSource(context: Context) : LocationSource {
    private val appContext = context.applicationContext
    private val manager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override suspend fun currentLocation(): Result<GeoPoint> {
        val fine = ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fine && !coarse) return Result.failure(IllegalStateException("নামাজের সময় ও কিবলা হিসাবের জন্য অবস্থানের অনুমতি দিন।"))
        return try {
            val provider = when {
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                fine && manager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                else -> return Result.failure(IllegalStateException("ফোনের অবস্থান চালু করে আবার চেষ্টা করুন।"))
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
            if (point == null) Result.failure(IllegalStateException("অবস্থান পাওয়া যায়নি। অবস্থানের সেটিংস দেখে আবার চেষ্টা করুন।"))
            else Result.success(point)
        } catch (_: SecurityException) {
            Result.failure(IllegalStateException("অবস্থানের অনুমতি দেওয়া হয়নি বা বাতিল করা হয়েছে।"))
        } catch (_: IllegalArgumentException) {
            Result.failure(IllegalStateException("অবস্থান সেবা পাওয়া যাচ্ছে না। ফোনের সেটিংস দেখুন।"))
        }
    }
}
