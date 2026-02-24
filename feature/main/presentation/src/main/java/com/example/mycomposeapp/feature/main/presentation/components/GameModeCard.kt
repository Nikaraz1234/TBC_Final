package com.example.mycomposeapp.feature.main.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.main.presentation.model.GameMode

@Composable
fun GameModeCard(
    gameMode: GameMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val radius = AppTheme.radius
    val shape = radius.radius12

    val contentAlpha = if (gameMode.isAvailable) 1f else 0.5f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(brush = colors.glassGradient, shape = shape)
            .border(1.dp, colors.glassBorder, shape)
            .clickable(enabled = true, onClick = onClick)
            .alpha(contentAlpha)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = gameMode.iconRes),
            contentDescription = gameMode.name,
            tint = colors.goldenYellow,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = gameMode.name,
                color = colors.textLight,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = gameMode.description,
                color = colors.textMuted,
                fontSize = 12.sp
            )
        }

        if (!gameMode.isAvailable) {
            Text(
                text = "SOON",
                color = colors.goldenYellow,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        color = colors.goldenYellow.copy(alpha = 0.2f),
                        shape = radius.radius8
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
