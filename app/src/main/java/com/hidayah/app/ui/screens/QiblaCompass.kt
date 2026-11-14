package com.hidayah.app.ui.screens

import android.content.Context
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.hidayah.app.data.GeoPoint
import com.hidayah.app.ui.theme.*
import kotlin.math.roundToInt

private data class CompassReading(val heading: Float? = null, val accuracy: Int = SensorManager.SENSOR_STATUS_UNRELIABLE, val available: Boolean = true)

/** Heading is relative to true north; the screen's top edge is the reference direction. */
@Composable
private fun rememberCompassReading(point: GeoPoint): CompassReading {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val manager = remember(context) { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val sensor = remember(manager) { manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) }
    var reading by remember(point, sensor) { mutableStateOf(CompassReading(available = sensor != null)) }
    DisposableEffect(lifecycleOwner, point, sensor) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val rotation = FloatArray(9)
                val adjusted = FloatArray(9)
                val orientation = FloatArray(3)
                SensorManager.getRotationMatrixFromVector(rotation, event.values)
                @Suppress("DEPRECATION")
                val displayRotation = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
                val (x, y) = when (displayRotation) {
                    Surface.ROTATION_90 -> SensorManager.AXIS_Y to SensorManager.AXIS_MINUS_X
                    Surface.ROTATION_180 -> SensorManager.AXIS_MINUS_X to SensorManager.AXIS_MINUS_Y
                    Surface.ROTATION_270 -> SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_X
                    else -> SensorManager.AXIS_X to SensorManager.AXIS_Y
                }
                if (!SensorManager.remapCoordinateSystem(rotation, x, y, adjusted)) return
                SensorManager.getOrientation(adjusted, orientation)
                val magneticHeading = Math.toDegrees(orientation[0].toDouble()).toFloat()
                val declination = GeomagneticField(point.latitude.toFloat(), point.longitude.toFloat(), point.altitudeMeters.toFloat(), System.currentTimeMillis()).declination
                val trueHeading = (magneticHeading + declination + 360f) % 360f
                reading = CompassReading(trueHeading, event.accuracy, true)
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { reading = reading.copy(accuracy = accuracy) }
        }
        fun start() {
            if (sensor != null && !manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)) {
                reading = reading.copy(available = false)
            }
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> start()
                Lifecycle.Event.ON_PAUSE -> manager.unregisterListener(listener)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { manager.unregisterListener(listener); lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    return reading
}

/** Uses Adhan's geographic bearing and the rotation-vector sensor corrected for magnetic declination. */
@Composable
fun QiblaCompass(point: GeoPoint, bearing: Double) {
    val reading = rememberCompassReading(point)
    val heading = reading.heading
    val angle = if (heading == null) null else ((bearing - heading + 360.0) % 360.0).toFloat()
    Column(Modifier.fillMaxWidth().background(CardSurface, RoundedCornerShape(22.dp)).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("প্রকৃত উত্তর থেকে ${banglaDigits((bearing.roundToInt() % 360).toString())}°", color = Forest, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        if (!reading.available) {
            Spacer(Modifier.height(12.dp))
            Text("এই ডিভাইসে প্রয়োজনীয় কম্পাস সেন্সর নেই। বিশ্বস্ত কম্পাসে উপরের দিক ব্যবহার করুন।", color = Muted, fontSize = 13.sp, lineHeight = 19.sp, textAlign = TextAlign.Center)
        } else if (angle == null) {
            Spacer(Modifier.height(12.dp))
            Text("কম্পাস সেন্সরের জন্য অপেক্ষা করা হচ্ছে…", color = Muted, fontSize = 13.sp)
        } else {
            Spacer(Modifier.height(12.dp))
            Text("ফোনের উপরের অংশ তীরের দিকে রাখুন", color = Muted, fontSize = 12.sp, textAlign = TextAlign.Center)
            val ringColor = Sage
            val markerColor = Gold
            val needleColor = Forest
            Canvas(Modifier.size(180.dp).padding(8.dp)) {
                val radius = size.minDimension / 2f
                drawCircle(color = ringColor, radius = radius, style = Stroke(width = 2.dp.toPx()))
                drawCircle(color = ringColor, radius = radius * .78f, style = Stroke(width = 1.dp.toPx()))
                drawLine(markerColor, Offset(center.x, center.y - radius), Offset(center.x, center.y - radius + 12.dp.toPx()), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                rotate(angle) {
                    drawLine(needleColor, Offset(center.x, center.y + radius * .38f), Offset(center.x, center.y - radius * .45f), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
                    val arrow = Path().apply {
                        moveTo(center.x, center.y - radius * .66f)
                        lineTo(center.x - 10.dp.toPx(), center.y - radius * .36f)
                        lineTo(center.x + 10.dp.toPx(), center.y - radius * .36f)
                        close()
                    }
                    drawPath(arrow, needleColor)
                    drawCircle(needleColor, radius = 5.dp.toPx(), center = center)
                }
            }
            Text("ফোনের দিক: ${banglaDigits((heading!!.roundToInt() % 360).toString())}°", color = Muted, fontSize = 12.sp)
            if (reading.accuracy <= SensorManager.SENSOR_STATUS_ACCURACY_LOW) {
                Spacer(Modifier.height(10.dp))
                Text("কম্পাসের নির্ভুলতা কম। ধাতব বস্তু থেকে দূরে গিয়ে ফোন আটের মতো ঘুরিয়ে ক্যালিব্রেট করুন।", color = androidx.compose.ui.graphics.Color(0xFF9D3E30), fontSize = 12.sp, lineHeight = 18.sp, textAlign = TextAlign.Center)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("ফোন সমতল রাখুন। চৌম্বকীয় প্রভাবে দিকের নির্ভুলতা কমতে পারে; বিশ্বস্ত উৎসে যাচাই করুন।", color = Muted, fontSize = 12.sp, lineHeight = 18.sp, textAlign = TextAlign.Center)
    }
}
