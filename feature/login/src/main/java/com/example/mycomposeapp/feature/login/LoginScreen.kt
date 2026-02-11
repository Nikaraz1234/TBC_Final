package com.example.mycomposeapp.feature.login

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycomposeapp.core.ui.R
import com.example.mycomposeapp.core.ui.components.buttons.ButtonLarge
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.components.input.AppTextField
import com.example.mycomposeapp.core.ui.components.input.PasswordTextField
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.feature.login.LoginContract.Event
import com.example.mycomposeapp.feature.login.LoginContract.SideEffect
import com.example.mycomposeapp.feature.login.LoginContract.State
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is SideEffect.NavigateToDashboard -> {
                    onNavigateToDashboard()
                }
                is SideEffect.NavigateToRegister -> {
                    onNavigateToRegister()
                }
                is SideEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AppTheme.colors.transparent
    ) { paddingValues ->
        LoginContent(
            state = state,
            onEvent = viewModel::onEvent,
            modifier = Modifier.padding(paddingValues)
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
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.spacing24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            Text(
                text = "Welcome Back",
                style = TextStyle(
                    brush = colors.goldTextGradient,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "Sign in to continue",
                color = colors.textMuted,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(spacing.spacing32))

            AppTextField(
                value = state.email,
                onValueChange = { onEvent(Event.OnEmailChanged(it)) },
                label = "Email",
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
                label = "Password",
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
                        text = "Remember me",
                        color = colors.textMuted,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "Forgot Password?",
                    color = colors.goldenYellow,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(enabled = !state.isLoading) {
                        onEvent(Event.OnForgotPasswordClicked)
                    }
                )
            }

            Spacer(modifier = Modifier.height(spacing.spacing24))

            if (state.generalError != null) {
                Text(
                    text = state.generalError,
                    color = androidx.compose.ui.graphics.Color(0xFFCF6679),
                    fontSize = 14.sp,
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
                    text = "Login",
                    onClick = { onEvent(Event.OnLoginClicked) },
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
                    text = "Don't have an account? ",
                    color = colors.textMuted,
                    fontSize = 14.sp
                )
                Text(
                    text = "Register",
                    color = colors.goldenYellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
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
