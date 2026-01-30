package com.example.mycomposeapp.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val Transparent = Color(0x00000000)



val GoldGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF7A5A1E),
        Color(0xFFC9A84E),
        Color(0xFFFFE08A),
        Color(0xFFD4AF37),
        Color(0xFF8A6A2A)
    )
)


//Splash Colors
val SplashOverlayTop = Black.copy(alpha = 0.35f)
val SplashOverlayBottom = Black.copy(alpha = 0.55f)

val SplashTitleGold = Color(0xFFFFC83D)
val SplashInitTitle = Color(0xFFE6E6E6)
val SplashInitSubtitle = Color(0xFFB0B0B0)

val SplashProgressTrack = Color(0xFF2A2F36)