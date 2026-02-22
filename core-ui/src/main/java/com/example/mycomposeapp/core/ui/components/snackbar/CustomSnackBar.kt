package com.example.mycomposeapp.core.ui.components.snackbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.R

@Composable
fun CustomSnackBar(data: SnackbarData) {
    val colors = AppTheme.colors
    val radius = AppTheme.radius
    val spacing = AppTheme.spacing
    val typography = AppTheme.typography

    val iconRes = R.drawable.app_logo

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.spacing16)
            .padding(bottom = spacing.spacing12)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, radius.radius16, clip = false)
                .background(
                    color = colors.backgroundDark,
                    shape = radius.radius16
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.onBackground, CircleShape)
            ) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = data.visuals.message,
                color = colors.textLight,
                style = typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 36.dp)
            )
        }
    }
}