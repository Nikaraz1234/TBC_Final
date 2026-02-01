package com.example.mycomposeapp.ui.components.buttons

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mycomposeapp.ui.theme.AppTheme
import com.example.mycomposeapp.ui.theme.MyComposeAppTheme

@Composable
fun ButtonSmall(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.Filled,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val colors = AppTheme.colors
    val radius = AppTheme.radius

    val shape = radius.radius20

    when (style) {
        ButtonStyle.Filled -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(shape)
                    .background(brush = colors.glassGradient, shape = shape)
                    .border(1.dp, colors.goldenYellow, shape)
                    .clickable(enabled = enabled, onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                ButtonContentSmall(
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
                    .height(40.dp)
                    .clip(shape)
                    .background(colors.transparent)
                    .border(1.dp, colors.goldenYellow, shape)
                    .clickable(enabled = enabled, onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                ButtonContentSmall(
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
                    .height(40.dp)
                    .clip(shape)
                    .background(brush = colors.socialGlassGradient, shape = shape)
                    .border(1.dp, colors.socialGlassBorder, shape)
                    .clickable(enabled = enabled, onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                ButtonContentSmall(
                    text = text,
                    textColor = colors.white,
                    leadingIcon = leadingIcon
                )
            }
        }
    }
}

@Composable
private fun ButtonContentSmall(
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
            Spacer(modifier = Modifier.width(8.dp))
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
private fun ButtonSmallFilledPreview() {
    MyComposeAppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ButtonSmall(
                text = "Login",
                onClick = {},
                style = ButtonStyle.Filled
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun ButtonSmallOutlinedPreview() {
    MyComposeAppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ButtonSmall(
                text = "Register",
                onClick = {},
                style = ButtonStyle.Outlined
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun ButtonSmallSocialPreview() {
    MyComposeAppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ButtonSmall(
                text = "Continue with Google",
                onClick = {},
                style = ButtonStyle.Social,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun ButtonSmallAllStylesPreview() {
    MyComposeAppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ButtonSmall(
                text = "Filled Button",
                onClick = {},
                style = ButtonStyle.Filled
            )
            ButtonSmall(
                text = "Outlined Button",
                onClick = {},
                style = ButtonStyle.Outlined
            )
            ButtonSmall(
                text = "Social Button",
                onClick = {},
                style = ButtonStyle.Social,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            )
        }
    }
}
