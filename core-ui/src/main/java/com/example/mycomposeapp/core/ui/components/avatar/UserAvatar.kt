package com.example.mycomposeapp.core.ui.components.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.ui.theme.AppTheme

private const val ICON_SIZE_RATIO = 0.6f

@Composable
fun UserAvatar(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = AppTheme.spacing.spacing48
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing

    if (imageUrl != null) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "User avatar",
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .border(spacing.spacing2, colors.goldenYellow, CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(colors.glassWhite, CircleShape)
                .border(spacing.spacing2, colors.goldenYellow, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Default avatar",
                tint = colors.textLight,
                modifier = Modifier.size(size * ICON_SIZE_RATIO)
            )
        }
    }
}
