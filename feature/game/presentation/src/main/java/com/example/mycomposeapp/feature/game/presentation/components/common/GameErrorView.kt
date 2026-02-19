package com.example.mycomposeapp.feature.game.presentation.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.mycomposeapp.core.ui.components.buttons.ButtonLarge
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.presentation.R as GameR

@Composable
fun GameErrorView(
    message: String,
    onRetry: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "\u26A0\uFE0F",
            style = typography.displayMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(GameR.string.error_title),
            color = colors.textLight,
            style = typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            color = colors.textMuted,
            style = typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        ButtonLarge(
            text = stringResource(GameR.string.btn_try_again),
            onClick = onRetry,
            style = ButtonStyle.Filled
        )

        Spacer(modifier = Modifier.height(12.dp))

        ButtonLarge(
            text = stringResource(GameR.string.btn_go_back),
            onClick = onExit,
            style = ButtonStyle.Outlined
        )
    }
}
