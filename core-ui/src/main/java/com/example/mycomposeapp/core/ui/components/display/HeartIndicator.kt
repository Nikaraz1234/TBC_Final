package com.example.mycomposeapp.core.ui.components.display

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun HeartIndicator(
    remaining: Int,
    total: Int = 3,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp
) {
    Text(
        text = "\u2764\uFE0F".repeat(remaining) + "\uD83D\uDDA4".repeat((total - remaining).coerceAtLeast(0)),
        fontSize = fontSize,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}
