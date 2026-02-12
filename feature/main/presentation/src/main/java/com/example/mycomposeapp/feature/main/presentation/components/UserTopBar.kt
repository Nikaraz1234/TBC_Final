package com.example.mycomposeapp.feature.main.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.feature.main.presentation.R as MainR
import com.example.mycomposeapp.core.ui.components.avatar.UserAvatar
import com.example.mycomposeapp.core.ui.components.badges.LevelBadge
import com.example.mycomposeapp.core.ui.components.display.CoinDisplay
import com.example.mycomposeapp.core.ui.theme.AppTheme

@Composable
fun UserTopBar(
    user: User?,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.spacing16, vertical = spacing.spacing12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onProfileClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(
                imageUrl = user?.photoUrl,
                size = spacing.spacing48
            )

            Spacer(modifier = Modifier.width(spacing.spacing12))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = user?.username ?: stringResource(MainR.string.guest),
                    color = colors.textLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                LevelBadge(level = user?.stats?.level ?: 1)
            }
        }

        CoinDisplay(coins = user?.stats?.coins ?: 0)
    }
}
