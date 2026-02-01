package com.example.mycomposeapp.ui.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.ui.theme.AppTheme
import com.example.mycomposeapp.ui.theme.MyComposeAppTheme

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true
) {
    val colors = AppTheme.colors
    val radius = AppTheme.radius
    val spacing = AppTheme.spacing

    val shape = radius.radius12
    val borderColor = if (error != null) Color(0xFFCF6679) else colors.glassBorder

    Column(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = label, color = colors.textMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.glassWhite, shape)
                .border(1.dp, borderColor, shape),
            colors = TextFieldDefaults.colors(
                focusedTextColor = colors.textLight,
                unfocusedTextColor = colors.textLight,
                disabledTextColor = colors.textMuted,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                cursorColor = colors.goldenYellow,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedLabelColor = colors.goldenYellow,
                unfocusedLabelColor = colors.textMuted
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = keyboardActions,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            enabled = enabled,
            singleLine = singleLine,
            isError = error != null
        )

        if (error != null) {
            Text(
                text = error,
                color = Color(0xFFCF6679),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = spacing.spacing8, top = spacing.spacing4)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun AppTextFieldPreview() {
    MyComposeAppTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            AppTextField(
                value = "",
                onValueChange = {},
                label = "Email"
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun AppTextFieldWithErrorPreview() {
    MyComposeAppTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            AppTextField(
                value = "invalid",
                onValueChange = {},
                label = "Email",
                error = "Please enter a valid email"
            )
        }
    }
}
