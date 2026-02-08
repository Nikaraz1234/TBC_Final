package com.example.mycomposeapp.feature.main.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.ui.components.avatar.UserAvatar
import com.example.mycomposeapp.core.ui.components.badges.LevelBadge
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.main.presentation.R
import java.text.NumberFormat

@Composable
fun UserTopBar(
    user: User?,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
                size = 48.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = user?.username ?: "Guest",
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

@Composable
private fun CoinDisplay(
    coins: Int,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val formattedCoins = NumberFormat.getNumberInstance().format(coins)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_coin),
            contentDescription = "Coins",
            tint = colors.goldenYellow,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = formattedCoins,
            color = colors.goldenYellow,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
