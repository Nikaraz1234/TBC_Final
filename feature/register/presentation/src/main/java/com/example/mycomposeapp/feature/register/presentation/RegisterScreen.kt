package com.example.mycomposeapp.feature.register.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions as ComposeKeyboardActions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.ui.R as CoreUiR
import com.example.mycomposeapp.feature.register.presentation.R as RegisterR
import com.example.mycomposeapp.core.ui.components.buttons.ButtonLarge
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.components.input.AppTextField
import com.example.mycomposeapp.core.ui.components.input.PasswordTextField
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.feature.register.presentation.RegisterContract.Event
import com.example.mycomposeapp.feature.register.presentation.RegisterContract.SideEffect
import com.example.mycomposeapp.feature.register.presentation.RegisterContract.State
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is SideEffect.NavigateToDashboard -> onNavigateToDashboard()
                is SideEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    RegisterContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun RegisterContent(
    state: State,
    onEvent: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val typography = AppTheme.typography
    val focusManager = LocalFocusManager.current

    if (state.showSuccessDialog) {
        SuccessDialog(
            onDismiss = { onEvent(Event.OnSuccessDialogDismissed) }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        AsyncImage(
            model = CoreUiR.drawable.app_background,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.spacing24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(spacing.spacing32))

            AsyncImage(
                model = CoreUiR.drawable.app_logo,
                contentDescription = stringResource(RegisterR.string.app_logo_desc),
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            Text(
                text = stringResource(RegisterR.string.create_account),
                style = typography.titleLarge.merge(
                    TextStyle(
                        brush = colors.goldTextGradient,
                        fontSize = 28.sp, 
                        fontWeight = FontWeight.Bold
                    )
                )
            )

            Text(
                text = stringResource(RegisterR.string.sign_up_to_get_started),
                color = colors.textMuted,
                style = typography.bodySmall
            )

            Spacer(modifier = Modifier.height(spacing.spacing24))

            AppTextField(
                value = state.fullName,
                onValueChange = { onEvent(Event.OnFullNameChanged(it)) },
                label = stringResource(RegisterR.string.label_full_name),
                error = state.fullNameError,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            AppTextField(
                value = state.email,
                onValueChange = { onEvent(Event.OnEmailChanged(it)) },
                label = stringResource(RegisterR.string.label_email),
                error = state.emailError,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            PasswordTextField(
                value = state.password,
                onValueChange = { onEvent(Event.OnPasswordChanged(it)) },
                label = stringResource(RegisterR.string.label_password),
                error = state.passwordError,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            PasswordTextField(
                value = state.confirmPassword,
                onValueChange = { onEvent(Event.OnConfirmPasswordChanged(it)) },
                label = stringResource(RegisterR.string.label_confirm_password),
                error = state.confirmPasswordError,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onEvent(Event.OnRegisterClicked)
                    }
                ),
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(enabled = !state.isLoading) {
                        onEvent(Event.OnTermsAcceptedChanged(!state.termsAccepted))
                    }
                ) {
                    Checkbox(
                        checked = state.termsAccepted,
                        onCheckedChange = { onEvent(Event.OnTermsAcceptedChanged(it)) },
                        enabled = !state.isLoading,
                        colors = CheckboxDefaults.colors(
                            checkedColor = colors.goldenYellow,
                            uncheckedColor = colors.textMuted,
                            checkmarkColor = colors.backgroundDark
                        )
                    )

                    Text(
                        text = stringResource(RegisterR.string.terms_agreement),
                        color = colors.textMuted,
                        style = typography.bodyMedium
                    )
                }

                if (state.termsError != null) {
                    Text(
                        text = state.termsError,
                        color = colors.error,
                        style = typography.bodySmall,
                        modifier = Modifier.padding(start = spacing.spacing8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.spacing24))

            if (state.generalError != null) {
                Text(
                    text = state.generalError,
                    color = colors.error,
                    style = typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = spacing.spacing16)
                )
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    color = colors.goldenYellow,
                    modifier = Modifier.size(48.dp)
                )
            } else {
                ButtonLarge(
                    text = stringResource(RegisterR.string.btn_register),
                    onClick = { onEvent(Event.OnRegisterClicked) },
                    style = ButtonStyle.Filled,
                    enabled = !state.isLoading
                )
            }

            Spacer(modifier = Modifier.height(spacing.spacing24))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(RegisterR.string.already_have_account),
                    color = colors.textMuted,
                    style = typography.bodyMedium
                )

                Text(
                    text = stringResource(RegisterR.string.btn_login),
                    color = colors.goldenYellow,
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable(enabled = !state.isLoading) {
                        onEvent(Event.OnLoginClicked)
                    }
                )
            }

            Spacer(modifier = Modifier.height(spacing.spacing32))
        }
    }
}

@Composable
private fun SuccessDialog(
    onDismiss: () -> Unit
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.backgroundDark,
        title = {
            Text(
                text = stringResource(RegisterR.string.registration_successful),
                style = typography.titleMedium.merge(
                    TextStyle(
                        brush = colors.goldTextGradient,
                        fontWeight = FontWeight.Bold
                    )
                )
            )
        },
        text = {
            Text(
                text = stringResource(RegisterR.string.registration_success_message),
                color = colors.textLight,
                style = typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(RegisterR.string.btn_continue),
                    color = colors.goldenYellow,
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenPreview() {
    MyComposeAppTheme {
        RegisterContent(
            state = State(),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenWithErrorsPreview() {
    MyComposeAppTheme {
        RegisterContent(
            state = State(
                fullName = "J",
                fullNameError = "Name must be at least 2 characters",
                email = "invalid",
                emailError = "Please enter a valid email",
                termsError = "You must accept the Terms & Conditions"
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SuccessDialogPreview() {
    MyComposeAppTheme {
        SuccessDialog(onDismiss = {})
    }
}
