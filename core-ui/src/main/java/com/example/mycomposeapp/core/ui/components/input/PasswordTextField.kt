package com.example.mycomposeapp.core.ui.components.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mycomposeapp.core.ui.R
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme

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
    var passwordVisible by remember { mutableStateOf(false) }

    BaseTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        error = error,
        keyboardType = KeyboardType.Password,
        imeAction = imeAction,
        keyboardActions = keyboardActions,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    painter = painterResource(
                        id = if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                    ),
                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                    tint = colors.textMuted
                )
            }
        },
        enabled = enabled,
        singleLine = true
    )
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
