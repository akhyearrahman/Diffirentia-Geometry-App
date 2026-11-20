package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class MathCanvasPalette(
    val canvasBackground: Color,
    val gridMinor: Color,
    val gridMajor: Color,
    val axisLine: Color,
    val vectorU: Color,
    val vectorV: Color,
    val projectionShadow: Color,
    val obtuseRose: Color,
    val metricViolet: Color,
    val sunbeam: Color,
    val labelText: Color,
    val tangentPlaneFill: Color
)

private val DarkMathPalette = MathCanvasPalette(
    canvasBackground = Color(0xFF060A12),
    gridMinor = Color(0xFF131D30),
    gridMajor = Color(0xFF1F2F4D),
    axisLine = Color(0xFF475D85),
    vectorU = ElectricCyan,
    vectorV = WarmAmber,
    projectionShadow = EmeraldMint,
    obtuseRose = CoralRose,
    metricViolet = MetricViolet,
    sunbeam = Color(0x66FDE68A),
    labelText = InkPrimaryDark,
    tangentPlaneFill = Color(0x3338BDF8)
)

private val LightMathPalette = MathCanvasPalette(
    canvasBackground = Color(0xFFF8FAFC),
    gridMinor = Color(0xFFE2E8F0),
    gridMajor = Color(0xFFCBD5E1),
    axisLine = Color(0xFF64748B),
    vectorU = CyanLight,
    vectorV = AmberLight,
    projectionShadow = EmeraldLight,
    obtuseRose = RoseLight,
    metricViolet = VioletLight,
    sunbeam = Color(0x88D97706),
    labelText = InkPrimaryLight,
    tangentPlaneFill = Color(0x260284C7)
)

val LocalMathPalette = staticCompositionLocalOf { DarkMathPalette }

private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = OnElectricCyan,
    primaryContainer = ElectricCyanContainer,
    onPrimaryContainer = ElectricCyan,
    secondary = WarmAmber,
    onSecondary = OnWarmAmber,
    secondaryContainer = WarmAmberContainer,
    onSecondaryContainer = WarmAmber,
    tertiary = EmeraldMint,
    onTertiary = OnEmeraldMint,
    tertiaryContainer = EmeraldMintContainer,
    onTertiaryContainer = EmeraldMint,
    error = CoralRose,
    errorContainer = CoralRoseContainer,
    background = ObsidianBackground,
    onBackground = InkPrimaryDark,
    surface = ObsidianSurface,
    onSurface = InkPrimaryDark,
    surfaceVariant = ObsidianSurfaceHigh,
    onSurfaceVariant = InkSecondaryDark,
    surfaceContainerLowest = ObsidianBackground,
    surfaceContainerLow = ObsidianSurface,
    surfaceContainer = ObsidianSurfaceHigh,
    surfaceContainerHigh = ObsidianSurfaceHighest,
    outline = ObsidianOutline,
    outlineVariant = ObsidianOutlineVariant
)

private val LightColorScheme = lightColorScheme(
    primary = CyanLight,
    onPrimary = Color.White,
    primaryContainer = CyanContainerLight,
    onPrimaryContainer = Color(0xFF075985),
    secondary = AmberLight,
    onSecondary = Color.White,
    secondaryContainer = AmberContainerLight,
    onSecondaryContainer = Color(0xFF92400E),
    tertiary = EmeraldLight,
    onTertiary = Color.White,
    tertiaryContainer = EmeraldContainerLight,
    onTertiaryContainer = Color(0xFF065F46),
    error = RoseLight,
    background = DaylightBackground,
    onBackground = InkPrimaryLight,
    surface = DaylightSurface,
    onSurface = InkPrimaryLight,
    surfaceVariant = DaylightSurfaceHigh,
    onSurfaceVariant = InkSecondaryLight,
    surfaceContainerLowest = DaylightSurface,
    surfaceContainerLow = DaylightBackground,
    surfaceContainer = DaylightSurfaceHigh,
    surfaceContainerHigh = DaylightSurfaceHighest,
    outline = DaylightOutline,
    outlineVariant = DaylightOutlineVariant
)

@Composable
fun GeoManifoldTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val mathPalette = if (darkTheme) DarkMathPalette else LightMathPalette

    CompositionLocalProvider(LocalMathPalette provides mathPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
