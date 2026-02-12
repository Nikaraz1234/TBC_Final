package com.example.mycomposeapp.core.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme

enum class ButtonSize(val height: Dp) {
    Large(56.dp),
    Medium(48.dp),
    Small(40.dp)
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Large,
    style: ButtonStyle = ButtonStyle.Filled,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val colors = AppTheme.colors
    val radius = AppTheme.radius
    val spacing = AppTheme.spacing

    val shape: Shape = when (size) {
        ButtonSize.Large -> radius.radius28
        ButtonSize.Medium -> radius.radius24
        ButtonSize.Small -> radius.radius20
    }

    val borderWidth = spacing.spacing1

    when (style) {
        ButtonStyle.Filled -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(size.height)
                    .clip(shape)
                    .background(brush = colors.glassGradient, shape = shape)
                    .border(borderWidth, colors.goldenYellow, shape)
                    .clickable(enabled = enabled, onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                ButtonContent(
                    text = text,
                    textBrush = colors.goldTextGradient,
                    leadingIcon = leadingIcon
                )
            }
        }
        ButtonStyle.Outlined -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(size.height)
                    .clip(shape)
                    .background(colors.transparent)
                    .border(borderWidth, colors.goldenYellow, shape)
                    .clickable(enabled = enabled, onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                ButtonContent(
                    text = text,
                    textBrush = colors.goldTextGradient,
                    leadingIcon = leadingIcon
                )
            }
        }
        ButtonStyle.Social -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(size.height)
                    .clip(shape)
                    .background(brush = colors.socialGlassGradient, shape = shape)
                    .border(borderWidth, colors.socialGlassBorder, shape)
                    .clickable(enabled = enabled, onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                ButtonContent(
                    text = text,
                    textColor = colors.white,
                    leadingIcon = leadingIcon
                )
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    textBrush: Brush? = null,
    textColor: Color? = null,
    leadingIcon: (@Composable () -> Unit)?
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(modifier = Modifier.width(AppTheme.spacing.spacing8))
        }
        if (textBrush != null) {
            Text(
                text = text,
                style = TextStyle(brush = textBrush)
            )
        } else {
            Text(
                text = text,
                color = textColor ?: Color.White
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun AppButtonAllSizesPreview() {
    MyComposeAppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppButton(text = "Large Filled", onClick = {}, size = ButtonSize.Large)
            AppButton(text = "Medium Outlined", onClick = {}, size = ButtonSize.Medium, style = ButtonStyle.Outlined)
            AppButton(
                text = "Small Social",
                onClick = {},
                size = ButtonSize.Small,
                style = ButtonStyle.Social,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = Color.White)
                }
            )
        }
    }
}
