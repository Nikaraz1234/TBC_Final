package com.example.mycomposeapp.core.ui.components.buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ButtonLarge(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.Filled,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    AppButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        size = ButtonSize.Large,
        style = style,
        enabled = enabled,
        leadingIcon = leadingIcon
    )
}
