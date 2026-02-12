package com.example.mycomposeapp.core.ui.components.display

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.core.ui.R
import com.example.mycomposeapp.core.ui.theme.AppTheme
import java.text.NumberFormat

@Composable
fun CoinDisplay(
    coins: Int,
    modifier: Modifier = Modifier,
    iconSize: Dp = AppTheme.spacing.spacing24,
    fontSize: TextUnit = 16.sp,
    formatted: Boolean = true
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val displayText = if (formatted) NumberFormat.getNumberInstance().format(coins) else "$coins"

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_coin),
            contentDescription = "Coins",
            tint = Color.Unspecified,
            modifier = Modifier.size(iconSize)
        )

        Spacer(modifier = Modifier.width(spacing.spacing6))

        Text(
            text = displayText,
            color = colors.goldenYellow,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}
