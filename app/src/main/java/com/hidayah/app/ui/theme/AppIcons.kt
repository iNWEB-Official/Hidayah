package com.hidayah.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Lightweight custom icons avoid the enormous extended Material icon library. */
object AppIcons {
    val Quran: ImageVector = ImageVector.Builder("Quran", 24.dp, 24.dp, 24f, 24f).apply {
        path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.7f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(12f, 20f); curveTo(9f, 18f, 5f, 18f, 2.8f, 19f); lineTo(2.8f, 5f); curveTo(5.5f, 3.8f, 9f, 4.2f, 12f, 6f)
            curveTo(15f, 4.2f, 18.5f, 3.8f, 21.2f, 5f); lineTo(21.2f, 19f); curveTo(19f, 18f, 15f, 18f, 12f, 20f); close()
            moveTo(12f, 6f); lineTo(12f, 20f)
        }
    }.build()
    val Prayer: ImageVector = ImageVector.Builder("Prayer", 24.dp, 24.dp, 24f, 24f).apply {
        path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.7f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(12f, 3f); curveTo(16.97f, 3f, 21f, 7.03f, 21f, 12f); curveTo(21f, 16.97f, 16.97f, 21f, 12f, 21f)
            curveTo(7.03f, 21f, 3f, 16.97f, 3f, 12f); curveTo(3f, 7.03f, 7.03f, 3f, 12f, 3f); close()
            moveTo(12f, 7f); lineTo(12f, 12f); lineTo(15.7f, 14.2f)
        }
    }.build()
}
