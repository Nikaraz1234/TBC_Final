package com.example.mycomposeapp.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColorScheme(
    // Background colors
    val backgroundDark: Color = Color(0xFF1A1A2E),
    val backgroundDarkEnd: Color = Color(0xFF16213E),

    // Primary accent colors
    val goldenYellow: Color = Color(0xFFD4AF37),
    val goldenYellowDark: Color = Color(0xFFB8960C),

    // Gold gradient colors for text
    val gold1: Color = Color(0xFF7A5A1E),
    val gold2: Color = Color(0xFFC9A84E),
    val gold3: Color = Color(0xFFFFE08A),
    val gold4: Color = Color(0xFFD4AF37),
    val gold5: Color = Color(0xFF8A6A2A),

    // Text colors
    val textLight: Color = Color(0xFFE8E8E8),
    val textMuted: Color = Color(0xFF8F8F8F),

    // Basic colors
    val white: Color = Color(0xFFFFFFFF),
    val black: Color = Color(0xFF000000),
    val transparent: Color = Color(0x00000000),

    // Glass-morphism colors for buttons
    val glassWhite: Color = Color.White.copy(alpha = 0.1f),
    val glassWhiteLight: Color = Color.White.copy(alpha = 0.15f),
    val glassWhiteDark: Color = Color.White.copy(alpha = 0.05f),
    val glassBorder: Color = Color.White.copy(alpha = 0.2f),

    // Social button glass colors
    val socialGlassBackground: Color = Color.White.copy(alpha = 0.15f),
    val socialGlassBorder: Color = Color.White.copy(alpha = 0.3f),

    // Splash screen colors
    val splashOverlayTop: Color = Color(0xFF000000).copy(alpha = 0.35f),
    val splashOverlayBottom: Color = Color(0xFF000000).copy(alpha = 0.55f),
    val splashTitleGold: Color = Color(0xFFFFC83D),
    val splashInitTitle: Color = Color(0xFFE6E6E6),
    val splashInitSubtitle: Color = Color(0xFFB0B0B0),
    val splashProgressTrack: Color = Color(0xFF2A2F36)
) {
    // Gradient brushes
    val backgroundGradient: Brush
        get() = Brush.verticalGradient(
            colors = listOf(backgroundDark, backgroundDarkEnd)
        )

    val glassGradient: Brush
        get() = Brush.verticalGradient(
            colors = listOf(glassWhiteLight, glassWhiteDark)
        )

    val socialGlassGradient: Brush
        get() = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.18f),
                Color.White.copy(alpha = 0.08f)
            )
        )

    val goldTextGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(gold1, gold2, gold3, gold4, gold5)
        )

    val splashOverlayGradient: Brush
        get() = Brush.verticalGradient(
            colors = listOf(splashOverlayTop, splashOverlayBottom)
        )
}

val LocalAppColorScheme = staticCompositionLocalOf { AppColorScheme() }
