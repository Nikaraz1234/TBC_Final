package com.example.mycomposeapp.feature.login.presentation

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.presentation.common.GoogleSignInLauncher
import com.example.mycomposeapp.core.ui.R as CoreUiR
import com.example.mycomposeapp.feature.login.presentation.R as LoginR
import com.example.mycomposeapp.core.ui.components.buttons.ButtonLarge
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.components.input.AppTextField
import com.example.mycomposeapp.core.ui.components.input.PasswordTextField
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.feature.login.presentation.LoginContract.Event
import com.example.mycomposeapp.feature.login.presentation.LoginContract.SideEffect
import com.example.mycomposeapp.feature.login.presentation.LoginContract.State
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    showSnackbar: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is SideEffect.NavigateToDashboard -> onNavigateToDashboard()
                is SideEffect.NavigateToRegister -> onNavigateToRegister()
                is SideEffect.ShowSnackbar -> showSnackbar
                is SideEffect.LaunchGoogleSignIn -> {
                    coroutineScope.launch {
                        when (val result = GoogleSignInLauncher.launch(context, "LoginScreen")) {
                            is GoogleSignInLauncher.Result.Success ->
                                viewModel.onEvent(Event.OnGoogleSignInResult(result.idToken))

                            is GoogleSignInLauncher.Result.Cancelled ->
                                viewModel.onEvent(Event.OnGoogleSignInResult(null))

                            is GoogleSignInLauncher.Result.Error ->
                                viewModel.onEvent(Event.OnGoogleSignInFailed(result.message))
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AppTheme.colors.transparent
    ) { _ ->
        LoginContent(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
private fun LoginContent(
    state: State,
    onEvent: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val typography = AppTheme.typography
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.spacing24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = CoreUiR.drawable.app_logo,
                contentDescription = stringResource(LoginR.string.app_logo_desc),
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            Text(
                text = stringResource(LoginR.string.welcome_back),
                style = typography.displaySmall.copy(
                    brush = colors.goldTextGradient,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = stringResource(LoginR.string.sign_in_to_continue),
                color = colors.textMuted,
                style = typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(spacing.spacing32))

            AppTextField(
                value = state.email,
                onValueChange = { onEvent(Event.OnEmailChanged(it)) },
                label = stringResource(LoginR.string.label_email),
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
                label = stringResource(LoginR.string.label_password),
                error = state.passwordError,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onEvent(Event.OnLoginClicked)
                    }
                ),
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(spacing.spacing8))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(enabled = !state.isLoading) {
                        onEvent(Event.OnRememberMeChanged(!state.rememberMe))
                    }
                ) {
                    Checkbox(
                        checked = state.rememberMe,
                        onCheckedChange = { onEvent(Event.OnRememberMeChanged(it)) },
                        enabled = !state.isLoading,
                        colors = CheckboxDefaults.colors(
                            checkedColor = colors.goldenYellow,
                            uncheckedColor = colors.textMuted,
                            checkmarkColor = colors.backgroundDark
                        )
                    )
                    Text(
                        text = stringResource(LoginR.string.remember_me),
                        color = colors.textMuted,
                        style = typography.bodyMedium
                    )
                }

                Text(
                    text = stringResource(LoginR.string.forgot_password),
                    color = colors.goldenYellow,
                    style = typography.bodyMedium,
                    modifier = Modifier.clickable(enabled = !state.isLoading) {
                        onEvent(Event.OnForgotPasswordClicked)
                    }
                )
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
                    text = stringResource(LoginR.string.btn_login),
                    onClick = { onEvent(Event.OnLoginClicked) },
                    style = ButtonStyle.Filled,
                    enabled = !state.isLoading && !state.isGoogleLoading
                )
            }

            Spacer(modifier = Modifier.height(spacing.spacing16))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = colors.textMuted.copy(alpha = 0.5f)
                )
                Text(
                    text = stringResource(LoginR.string.or_divider),
                    color = colors.textMuted,
                    style = typography.labelLarge,
                    modifier = Modifier.padding(horizontal = spacing.spacing16)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = colors.textMuted.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(spacing.spacing16))

            if (state.isGoogleLoading) {
                CircularProgressIndicator(
                    color = colors.goldenYellow,
                    modifier = Modifier.size(48.dp)
                )
            } else {
                ButtonLarge(
                    text = stringResource(LoginR.string.btn_continue_with_google),
                    onClick = { onEvent(Event.OnGoogleSignInClicked) },
                    style = ButtonStyle.Social,
                    enabled = !state.isLoading && !state.isGoogleLoading,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = CoreUiR.drawable.ic_google),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = colors.white
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(spacing.spacing24))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(LoginR.string.no_account_prompt),
                    color = colors.textMuted,
                    style = typography.bodyMedium
                )
                Text(
                    text = stringResource(LoginR.string.btn_register),
                    color = colors.goldenYellow,
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable(enabled = !state.isLoading) {
                        onEvent(Event.OnRegisterClicked)
                    }
                )
            }

            Spacer(modifier = Modifier.height(spacing.spacing32))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    MyComposeAppTheme {
        LoginContent(
            state = State(),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenWithErrorPreview() {
    MyComposeAppTheme {
        LoginContent(
            state = State(
                email = "test",
                emailError = "Please enter a valid email",
                generalError = "Invalid credentials"
            ),
            onEvent = {}
        )
    }
}
