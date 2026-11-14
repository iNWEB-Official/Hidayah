package com.hidayah.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.hidayah.app.R

val BanglaFont = FontFamily(Font(R.font.noto_bengali))
val ArabicFont = FontFamily(Font(R.font.noto_arabic))
val Forest: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
val DeepForest: Color @Composable @ReadOnlyComposable get() = if (MaterialTheme.colorScheme.background == Color(0xFF101B16)) Color(0xFF103A29) else Color(0xFF06472E)
val Sage: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceVariant
val Canvas: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.background
val Ink: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
val Muted: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant
val Gold = Color(0xFFEAC86C)
val Line: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outlineVariant
val CardSurface: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surface

private val Light = lightColorScheme(primary = Color(0xFF075C3A), onPrimary = Color.White,
    secondary = Color(0xFF94702C), background = Color(0xFFFAF8EE), onBackground = Color(0xFF172E23),
    surface = Color(0xFFFFFEF8), onSurface = Color(0xFF172E23), surfaceVariant = Color(0xFFE8F0E5),
    onSurfaceVariant = Color(0xFF5C6D60), outlineVariant = Color(0xFFE6E9DB))
private val Dark = darkColorScheme(primary = Color(0xFF94D8A8), onPrimary = Color(0xFF00391D),
    secondary = Color(0xFFE8C978), background = Color(0xFF101B16), onBackground = Color(0xFFE2EAE2),
    surface = Color(0xFF1C2C23), onSurface = Color(0xFFE2EAE2), surfaceVariant = Color(0xFF2C4033),
    onSurfaceVariant = Color(0xFFB7CAB9), outlineVariant = Color(0xFF405447))
private val BaseTypography = Typography()
private val BengaliTypography = Typography(
    displayLarge = BaseTypography.displayLarge.copy(fontFamily = BanglaFont),
    displayMedium = BaseTypography.displayMedium.copy(fontFamily = BanglaFont),
    displaySmall = BaseTypography.displaySmall.copy(fontFamily = BanglaFont),
    headlineLarge = BaseTypography.headlineLarge.copy(fontFamily = BanglaFont),
    headlineMedium = BaseTypography.headlineMedium.copy(fontFamily = BanglaFont),
    headlineSmall = BaseTypography.headlineSmall.copy(fontFamily = BanglaFont),
    titleLarge = BaseTypography.titleLarge.copy(fontFamily = BanglaFont),
    titleMedium = BaseTypography.titleMedium.copy(fontFamily = BanglaFont),
    titleSmall = BaseTypography.titleSmall.copy(fontFamily = BanglaFont),
    bodyLarge = BaseTypography.bodyLarge.copy(fontFamily = BanglaFont),
    bodyMedium = BaseTypography.bodyMedium.copy(fontFamily = BanglaFont),
    bodySmall = BaseTypography.bodySmall.copy(fontFamily = BanglaFont),
    labelLarge = BaseTypography.labelLarge.copy(fontFamily = BanglaFont),
    labelMedium = BaseTypography.labelMedium.copy(fontFamily = BanglaFont),
    labelSmall = BaseTypography.labelSmall.copy(fontFamily = BanglaFont),
)
@Composable
fun HidayahTheme(dark: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (dark) Dark else Light, typography = BengaliTypography, content = content)
}
