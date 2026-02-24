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
    val typography = AppTheme.typography

    val shape: Shape = when (size) {
        ButtonSize.Large -> radius.radius28
        ButtonSize.Medium -> radius.radius24
        ButtonSize.Small -> radius.radius20
    }

    val borderWidth = spacing.spacing1

    val textStyle: TextStyle = when (size) {
        ButtonSize.Large -> typography.labelLarge
        ButtonSize.Medium -> typography.labelMedium
        ButtonSize.Small -> typography.labelSmall
    }

    val disabledAlpha = 0.45f
    val contentAlpha = if (enabled) 1f else disabledAlpha

    when (style) {
        ButtonStyle.Filled -> {
            val bgBrush =
                if (enabled) {
                    colors.glassGradient
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            colors.glassWhiteLight.copy(alpha = colors.glassWhiteLight.alpha * disabledAlpha),
                            colors.glassWhiteDark.copy(alpha = colors.glassWhiteDark.alpha * disabledAlpha),
                        )
                    )
                }

            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(size.height)
                    .clip(shape)
                    .background(brush = bgBrush, shape = shape)
                    .border(
                        width = borderWidth,
                        color = (if (enabled) colors.goldenYellow else colors.goldenYellow.copy(alpha = disabledAlpha)),
                        shape = shape
                    )
                    .clickable(enabled = enabled, onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                ButtonContent(
                    text = text,
                    textStyle = textStyle,
                    textBrush = if (enabled) colors.goldTextGradient else null,
                    textColor = if (enabled) null else colors.textMuted,
                    leadingIcon = leadingIcon,
                    contentAlpha = contentAlpha
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
                    .border(
                        width = borderWidth,
                        color = (if (enabled) colors.goldenYellow else colors.goldenYellow.copy(alpha = disabledAlpha)),
                        shape = shape
                    )
                    .clickable(enabled = enabled, onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                ButtonContent(
                    text = text,
                    textStyle = textStyle,
                    textBrush = if (enabled) colors.goldTextGradient else null,
                    textColor = if (enabled) null else colors.textMuted,
                    leadingIcon = leadingIcon,
                    contentAlpha = contentAlpha
                )
            }
        }

        ButtonStyle.Social -> {
            val bgBrush =
                if (enabled) {
                    colors.socialGlassGradient
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            colors.socialGlassBackground.copy(alpha = colors.socialGlassBackground.alpha * disabledAlpha),
                            Color.Black.copy(alpha = 0.0f)
                        )
                    )
                }

            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(size.height)
                    .clip(shape)
                    .background(brush = bgBrush, shape = shape)
                    .border(
                        width = borderWidth,
                        color = (if (enabled) colors.socialGlassBorder else colors.socialGlassBorder.copy(alpha = disabledAlpha)),
                        shape = shape
                    )
                    .clickable(enabled = enabled, onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                ButtonContent(
                    text = text,
                    textStyle = textStyle,
                    textColor = colors.textLight,
                    leadingIcon = leadingIcon,
                    contentAlpha = contentAlpha
                )
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    textStyle: TextStyle,
    textBrush: Brush? = null,
    textColor: Color? = null,
    leadingIcon: (@Composable () -> Unit)?,
    contentAlpha: Float = 1f
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
                style = textStyle.copy(brush = textBrush).copy(color = Color.Unspecified),
                modifier = Modifier,
            )
        } else {
            Text(
                text = text,
                style = textStyle,
                color = (textColor ?: AppTheme.colors.textLight).copy(alpha = contentAlpha)
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

            AppButton(
                text = "Medium Outlined",
                onClick = {},
                size = ButtonSize.Medium,
                style = ButtonStyle.Outlined
            )

            AppButton(
                text = "Small Social",
                onClick = {},
                size = ButtonSize.Small,
                style = ButtonStyle.Social,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = AppTheme.colors.textLight
                    )
                }
            )

            AppButton(
                text = "Disabled Filled",
                onClick = {},
                enabled = false,
                size = ButtonSize.Large,
                style = ButtonStyle.Filled
            )
        }
    }
}