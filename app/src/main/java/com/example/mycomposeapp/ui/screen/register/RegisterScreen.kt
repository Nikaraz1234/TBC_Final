package com.example.mycomposeapp.ui.screen.register

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
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
import androidx.navigation.NavController
import com.example.mycomposeapp.R
import com.example.mycomposeapp.ui.components.buttons.ButtonLarge
import com.example.mycomposeapp.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.ui.components.input.AppTextField
import com.example.mycomposeapp.ui.components.input.PasswordTextField
import com.example.mycomposeapp.ui.screen.dashboard.navigation.DashboardRoute
import com.example.mycomposeapp.ui.screen.login.navigation.LoginRoute
import com.example.mycomposeapp.ui.screen.register.RegisterContract.Event
import com.example.mycomposeapp.ui.screen.register.RegisterContract.SideEffect
import com.example.mycomposeapp.ui.screen.register.RegisterContract.State
import com.example.mycomposeapp.ui.theme.AppTheme
import com.example.mycomposeapp.ui.theme.MyComposeAppTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is SideEffect.NavigateToDashboard -> {
                    navController.navigate(DashboardRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is SideEffect.NavigateToLogin -> {
                    navController.popBackStack()
                }
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
    val focusManager = LocalFocusManager.current

    if (state.showSuccessDialog) {
        SuccessDialog(
            onDismiss = { onEvent(Event.OnSuccessDialogDismissed) }
        )
    }

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
            Spacer(modifier = Modifier.height(spacing.spacing32))

            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            Text(
                text = "Create Account",
                style = TextStyle(
                    brush = colors.goldTextGradient,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "Sign up to get started",
                color = colors.textMuted,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(spacing.spacing24))

            AppTextField(
                value = state.fullName,
                onValueChange = { onEvent(Event.OnFullNameChanged(it)) },
                label = "Full Name",
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
                label = "Confirm Password",
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
                        text = "I agree to the Terms & Conditions",
                        color = colors.textMuted,
                        fontSize = 14.sp
                    )
                }
                if (state.termsError != null) {
                    Text(
                        text = state.termsError,
                        color = Color(0xFFCF6679),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = spacing.spacing8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.spacing24))

            if (state.generalError != null) {
                Text(
                    text = state.generalError,
                    color = Color(0xFFCF6679),
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
                    text = "Register",
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
                    text = "Already have an account? ",
                    color = colors.textMuted,
                    fontSize = 14.sp
                )
                Text(
                    text = "Login",
                    color = colors.goldenYellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
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

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.backgroundDark,
        title = {
            Text(
                text = "Registration Successful!",
                style = TextStyle(
                    brush = colors.goldTextGradient,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Text(
                text = "Your account has been created successfully. Welcome to AxisSolve!",
                color = colors.textLight,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Continue",
                    color = colors.goldenYellow,
                    fontWeight = FontWeight.Bold
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
