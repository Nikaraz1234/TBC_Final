package com.example.mycomposeapp.ui.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.R
import com.example.mycomposeapp.ui.theme.AppTheme
import com.example.mycomposeapp.ui.theme.MyComposeAppTheme

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean = true
) {
    val colors = AppTheme.colors
    val radius = AppTheme.radius
    val spacing = AppTheme.spacing

    var passwordVisible by remember { mutableStateOf(false) }

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
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = keyboardActions,
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible) {
                                R.drawable.ic_visibility_off
                            } else {
                                R.drawable.ic_visibility
                            }
                        ),
                        contentDescription = if (passwordVisible) {
                            "Hide password"
                        } else {
                            "Show password"
                        },
                        tint = colors.textMuted
                    )
                }
            },
            enabled = enabled,
            singleLine = true,
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
private fun PasswordTextFieldPreview() {
    MyComposeAppTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            PasswordTextField(
                value = "",
                onValueChange = {},
                label = "Password"
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun PasswordTextFieldWithErrorPreview() {
    MyComposeAppTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            PasswordTextField(
                value = "test",
                onValueChange = {},
                label = "Password",
                error = "Password must be at least 8 characters"
            )
        }
    }
}
