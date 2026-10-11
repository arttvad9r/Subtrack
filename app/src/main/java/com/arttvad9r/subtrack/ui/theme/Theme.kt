package com.arttvad9r.subtrack.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Moss = Color(0xFF35624C)
private val MossDark = Color(0xFFA9D3B8)
private val Clay = Color(0xFF795747)
private val Ink = Color(0xFF202A24)
private val Paper = Color(0xFFF5F7F2)

private val LightColors = lightColorScheme(
    primary = Moss,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0ECE2),
    onPrimaryContainer = Color(0xFF1F3325),
    inversePrimary = MossDark,
    secondary = Clay,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEADFD8),
    onSecondaryContainer = Color(0xFF3A241C),
    tertiary = Color(0xFF5B6B7A),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDFE6EC),
    onTertiaryContainer = Color(0xFF1E2830),
    background = Paper,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFEBEBE5),
    onSurfaceVariant = Color(0xFF535E56),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF7F7F4),
    surfaceContainer = Color(0xFFF3F3EF),
    surfaceContainerHigh = Color(0xFFEDEDE8),
    surfaceContainerHighest = Color(0xFFE7E7E2),
    outline = Color(0xFF7A7F7C),
    outlineVariant = Color(0xFFD6D9D3),
    error = Color(0xFF9E4B3E),
    onError = Color.White,
    errorContainer = Color(0xFFF5DDD8),
    onErrorContainer = Color(0xFF3E1A14),
    inverseSurface = Color(0xFF2F3336),
    inverseOnSurface = Color(0xFFF1F1ED),
    scrim = Color.Black,
)

private val DarkColors = darkColorScheme(
    primary = MossDark,
    onPrimary = Color(0xFF13251A),
    primaryContainer = Color(0xFF2C4434),
    onPrimaryContainer = Color(0xFFE0ECE2),
    inversePrimary = Moss,
    secondary = Color(0xFFD9A08C),
    onSecondary = Color(0xFF3A241C),
    secondaryContainer = Color(0xFF4A3A33),
    onSecondaryContainer = Color(0xFFEADFD8),
    tertiary = Color(0xFFAEBECD),
    onTertiary = Color(0xFF1E2830),
    tertiaryContainer = Color(0xFF343F49),
    onTertiaryContainer = Color(0xFFDFE6EC),
    background = Color(0xFF141B17),
    onBackground = Color(0xFFE8E9E6),
    surface = Color(0xFF1C2520),
    onSurface = Color(0xFFE8E9E6),
    surfaceVariant = Color(0xFF29362E),
    onSurfaceVariant = Color(0xFFC2C6C2),
    surfaceContainerLowest = Color(0xFF121914),
    surfaceContainerLow = Color(0xFF1C2520),
    surfaceContainer = Color(0xFF222D26),
    surfaceContainerHigh = Color(0xFF29362E),
    surfaceContainerHighest = Color(0xFF314035),
    outline = Color(0xFF6E7370),
    outlineVariant = Color(0xFF35433A),
    error = Color(0xFFE0A199),
    onError = Color(0xFF3E1A14),
    errorContainer = Color(0xFF5A2A22),
    onErrorContainer = Color(0xFFF5DDD8),
    inverseSurface = Color(0xFFE8E9E6),
    inverseOnSurface = Color(0xFF2F3336),
    scrim = Color.Black,
)

private val AppTypography = Typography(
    displaySmall = TextStyle(fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.SemiBold),
    headlineSmall = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontSize = 19.sp, lineHeight = 25.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 16.sp, lineHeight = 21.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontSize = 13.sp, lineHeight = 19.sp),
    labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium),
    labelMedium = TextStyle(fontSize = 11.5.sp, lineHeight = 15.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontSize = 11.5.sp, lineHeight = 15.sp, fontWeight = FontWeight.Medium),
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(26.dp),
)

@Composable
fun SubtrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
