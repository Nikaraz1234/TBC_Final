package com.example.mycomposeapp.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColorScheme(
    // Background colors
    val backgroundDark: Color,
    val backgroundDarkEnd: Color,

    // Primary accent colors
    val goldenYellow: Color,
    val goldenYellowDark: Color,

    // Gold gradient colors for text
    val gold1: Color,
    val gold2: Color,
    val gold3: Color,
    val gold4: Color,
    val gold5: Color,

    // Text colors
    val textLight: Color,
    val textMuted: Color,

    // Basic colors
    val white: Color,
    val black: Color,
    val transparent: Color,

    // Glass-morphism colors for buttons
    val glassWhite: Color,
    val glassWhiteLight: Color,
    val glassWhiteDark: Color,
    val glassBorder: Color,

    // Social button glass colors
    val socialGlassBackground: Color,
    val socialGlassBorder: Color,

    // Error color
    val error: Color,

    // Splash screen colors
    val splashOverlayTop: Color,
    val splashOverlayBottom: Color,
    val splashTitleGold: Color,
    val splashInitTitle: Color,
    val splashInitSubtitle: Color,
    val splashProgressTrack: Color
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
                socialGlassBackground,
                Color.Black.copy(alpha = 0.0f) // subtle fade; optional
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

    companion object {
        fun dark(): AppColorScheme = AppColorScheme(
            backgroundDark = Color(0xFF1A1A2E),
            backgroundDarkEnd = Color(0xFF16213E),

            goldenYellow = Color(0xFFD4AF37),
            goldenYellowDark = Color(0xFFB8960C),

            gold1 = Color(0xFF7A5A1E),
            gold2 = Color(0xFFC9A84E),
            gold3 = Color(0xFFFFE08A),
            gold4 = Color(0xFFD4AF37),
            gold5 = Color(0xFF8A6A2A),

            textLight = Color(0xFFE8E8E8),
            textMuted = Color(0xFF8F8F8F),

            white = Color(0xFFFFFFFF),
            black = Color(0xFF000000),
            transparent = Color(0x00000000),

            glassWhite = Color.White.copy(alpha = 0.10f),
            glassWhiteLight = Color.White.copy(alpha = 0.15f),
            glassWhiteDark = Color.White.copy(alpha = 0.05f),
            glassBorder = Color.White.copy(alpha = 0.20f),

            socialGlassBackground = Color.White.copy(alpha = 0.15f),
            socialGlassBorder = Color.White.copy(alpha = 0.30f),

            error = Color(0xFFCF6679),

            splashOverlayTop = Color(0xFF000000).copy(alpha = 0.35f),
            splashOverlayBottom = Color(0xFF000000).copy(alpha = 0.55f),
            splashTitleGold = Color(0xFFFFC83D),
            splashInitTitle = Color(0xFFE6E6E6),
            splashInitSubtitle = Color(0xFFB0B0B0),
            splashProgressTrack = Color(0xFF2A2F36)
        )

        fun light(): AppColorScheme = AppColorScheme(
            // Light backgrounds that still “feel” premium and close to your dark look
            backgroundDark = Color(0xFFF6F7FB),
            backgroundDarkEnd = Color(0xFFEDF0F8),

            // Keep gold identity (slightly brighter reads better on white)
            goldenYellow = Color(0xFFFFC83D),
            goldenYellowDark = Color(0xFFB8960C),

            // Same gradient family, but a bit brighter / cleaner for readability
            gold1 = Color(0xFF8A6A2A),
            gold2 = Color(0xFFD7B45B),
            gold3 = Color(0xFFFFE8A6),
            gold4 = Color(0xFFFFC83D),
            gold5 = Color(0xFF9A7A34),

            // Text flips (dark text on light background)
            textLight = Color(0xFF0F172A),     // used as “primary text” in your app
            textMuted = Color(0xFF5B6475),

            white = Color(0xFFFFFFFF),
            black = Color(0xFF000000),
            transparent = Color(0x00000000),

            // Glass on light: use dark tint (not white), otherwise it disappears
            glassWhite = Color(0xFF0F172A).copy(alpha = 0.06f),
            glassWhiteLight = Color(0xFF0F172A).copy(alpha = 0.08f),
            glassWhiteDark = Color(0xFF0F172A).copy(alpha = 0.03f),
            glassBorder = Color(0xFF0F172A).copy(alpha = 0.10f),

            socialGlassBackground = Color(0xFF0F172A).copy(alpha = 0.06f),
            socialGlassBorder = Color(0xFF0F172A).copy(alpha = 0.12f),

            error = Color(0xFFD92D20),

            // Splash can stay dark-themed even in light mode (common pattern)
            splashOverlayTop = Color(0xFF000000).copy(alpha = 0.25f),
            splashOverlayBottom = Color(0xFF000000).copy(alpha = 0.45f),
            splashTitleGold = Color(0xFFFFC83D),
            splashInitTitle = Color(0xFF111827),
            splashInitSubtitle = Color(0xFF6B7280),
            splashProgressTrack = Color(0xFFE2E8F0)
        )
    }
}

val LocalAppColorScheme = staticCompositionLocalOf { AppColorScheme.dark() }
