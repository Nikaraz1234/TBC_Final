package com.example.mycomposeapp.feature.welcome.presentation

import android.util.Log
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.domain.keys.GoogleAuthConstants
import com.example.mycomposeapp.core.ui.components.buttons.ButtonLarge
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.feature.welcome.presentation.WelcomeContract.Event
import com.example.mycomposeapp.feature.welcome.presentation.WelcomeContract.SideEffect
import com.example.mycomposeapp.feature.welcome.presentation.WelcomeContract.State
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.mycomposeapp.core.ui.R as CoreUiR

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: WelcomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is SideEffect.NavigateToLogin -> onNavigateToLogin()
                is SideEffect.NavigateToRegister -> onNavigateToRegister()
                is SideEffect.NavigateToDashboard -> onNavigateToDashboard()
                is SideEffect.LaunchGoogleSignIn -> {
                    coroutineScope.launch {
                        try {
                            val credentialManager = CredentialManager.create(context)
                            val signInOption = GetSignInWithGoogleOption.Builder(GoogleAuthConstants.WEB_CLIENT_ID)
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(signInOption)
                                .build()

                            val result = credentialManager.getCredential(context, request)
                            val credential = result.credential
                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                            val idToken = googleIdTokenCredential.idToken

                            viewModel.onEvent(Event.OnGoogleSignInResult(idToken))
                        } catch (e: GetCredentialCancellationException) {
                            viewModel.onEvent(Event.OnGoogleSignInResult(null))
                        } catch (e: NoCredentialException) {
                            Log.e("WelcomeScreen", "No credentials available", e)
                            viewModel.onEvent(Event.OnGoogleSignInFailed("Google Sign-In is not available. Please check app configuration."))
                        } catch (e: GetCredentialException) {
                            Log.e("WelcomeScreen", "Google sign-in failed", e)
                            viewModel.onEvent(Event.OnGoogleSignInFailed("Google sign-in failed: ${e.message}"))
                        }
                    }
                }
            }
        }
    }

    WelcomeContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun WelcomeContent(
    state: State,
    onEvent: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = CoreUiR.drawable.app_background,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.spacing8),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = CoreUiR.drawable.app_logo,
                contentDescription = "App Logo",
                modifier = Modifier.size(150.dp)
            )

            Text(
                text = "AxisSolve",
                style = TextStyle(
                    brush = colors.goldTextGradient,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(spacing.spacing48))

            ButtonLarge(
                text = "Login",
                onClick = { onEvent(Event.OnLoginClicked) },
                style = ButtonStyle.Filled,
                enabled = !state.isGoogleLoading
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            ButtonLarge(
                text = "Register",
                onClick = { onEvent(Event.OnRegisterClicked) },
                style = ButtonStyle.Outlined,
                enabled = !state.isGoogleLoading
            )

            Spacer(modifier = Modifier.height(spacing.spacing24))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = colors.textMuted
                )
                Text(
                    text = "OR",
                    color = colors.textMuted,
                    modifier = Modifier.padding(horizontal = spacing.spacing16),
                    fontSize = 14.sp
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = colors.textMuted
                )
            }

            Spacer(modifier = Modifier.height(spacing.spacing24))

            if (state.isGoogleLoading) {
                CircularProgressIndicator(
                    color = colors.goldenYellow,
                    modifier = Modifier.size(48.dp)
                )
            } else {
                ButtonLarge(
                    text = "Continue with Google",
                    onClick = { onEvent(Event.OnGoogleSignInClicked) },
                    style = ButtonStyle.Social,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = CoreUiR.drawable.ic_google),
                            contentDescription = "Google Icon",
                            modifier = Modifier.size(20.dp),
                            tint = colors.white
                        )
                    }
                )
            }

            if (state.generalError != null) {
                Spacer(modifier = Modifier.height(spacing.spacing16))
                Text(
                    text = state.generalError,
                    color = androidx.compose.ui.graphics.Color(0xFFCF6679),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(spacing.spacing32))

            Text(
                text = "By continuing, you agree to our Terms of Service",
                color = colors.textMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WelcomeScreenPreview() {
    MyComposeAppTheme {
        WelcomeContent(
            state = State(),
            onEvent = {}
        )
    }
}
