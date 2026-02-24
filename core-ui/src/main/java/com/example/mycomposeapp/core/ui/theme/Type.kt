package com.example.mycomposeapp.core.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.core.ui.R
import androidx.compose.material3.Typography as M3Typography
val LuckiestGuy = FontFamily(Font(R.font.luckiest_guy, FontWeight.Normal))
val Oswald = FontFamily(Font(R.font.oswald, FontWeight.Normal))
val Fredroka = FontFamily(Font(R.font.fredroka, FontWeight.Normal))

data class AppTypography(
    // DISPLAY
    val displayLarge: TextStyle,
    val displayMedium: TextStyle,
    val displaySmall: TextStyle,

    // HEADLINES
    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val headlineSmall: TextStyle,

    // TITLES
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,

    // BODY
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,

    // LABELS
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,
)

val DefaultAppTypography = AppTypography(
    displayLarge = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 48.sp),
    displayMedium = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 42.sp),
    displaySmall = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 36.sp),

    headlineLarge = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 28.sp),
    headlineMedium = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 24.sp),
    headlineSmall = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 20.sp),

    titleLarge = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 18.sp),
    titleSmall = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 15.sp),

    bodyLarge = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 12.sp),

    labelLarge = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 14.sp, letterSpacing = 0.3.sp),
    labelMedium = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 13.sp, letterSpacing = 0.3.sp),
    labelSmall = TextStyle(fontFamily = LuckiestGuy, fontWeight = FontWeight.Normal, fontSize = 12.sp, letterSpacing = 1.sp),
)

val LocalAppTypography = staticCompositionLocalOf<AppTypography> {
    error("LocalAppTypography not provided")
}

fun AppTypography.toMaterial3Typography(): M3Typography = M3Typography(
    displayLarge = displayLarge,
    displayMedium = displayMedium,
    displaySmall = displaySmall,

    headlineLarge = headlineLarge,
    headlineMedium = headlineMedium,
    headlineSmall = headlineSmall,

    titleLarge = titleLarge,
    titleMedium = titleMedium,
    titleSmall = titleSmall,

    bodyLarge = bodyLarge,
    bodyMedium = bodyMedium,
    bodySmall = bodySmall,

    labelLarge = labelLarge,
    labelMedium = labelMedium,
    labelSmall = labelSmall,
)