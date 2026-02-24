package com.example.mycomposeapp.feature.welcome.presentation

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.presentation.common.GoogleSignInLauncher
import com.example.mycomposeapp.core.ui.R as CoreUiR
import com.example.mycomposeapp.core.ui.components.buttons.ButtonLarge
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.feature.welcome.presentation.R as WelcomeR
import com.example.mycomposeapp.feature.welcome.presentation.WelcomeContract.Event
import com.example.mycomposeapp.feature.welcome.presentation.WelcomeContract.SideEffect
import com.example.mycomposeapp.feature.welcome.presentation.WelcomeContract.State
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: WelcomeViewModel = hiltViewModel(),
    showSnackbar: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
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
                        when (val result = GoogleSignInLauncher.launch(context, "WelcomeScreen")) {
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
    val typography = AppTheme.typography

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = spacing.spacing8),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = CoreUiR.drawable.app_logo,
                contentDescription = stringResource(WelcomeR.string.app_logo_desc),
                modifier = Modifier.size(150.dp)
            )

            Text(
                text = stringResource(WelcomeR.string.app_name_display),
                style = typography.displaySmall.copy(
                    brush = colors.goldTextGradient,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(spacing.spacing48))

            ButtonLarge(
                text = stringResource(WelcomeR.string.btn_login),
                onClick = { onEvent(Event.OnLoginClicked) },
                style = ButtonStyle.Filled,
                enabled = !state.isGoogleLoading
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            ButtonLarge(
                text = stringResource(WelcomeR.string.btn_register),
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
                    text = stringResource(WelcomeR.string.or_divider),
                    color = colors.textMuted,
                    style = typography.labelLarge,
                    modifier = Modifier.padding(horizontal = spacing.spacing16)
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
                    text = stringResource(WelcomeR.string.btn_continue_with_google),
                    onClick = { onEvent(Event.OnGoogleSignInClicked) },
                    style = ButtonStyle.Social,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = CoreUiR.drawable.ic_google),
                            contentDescription = stringResource(WelcomeR.string.google_icon_desc),
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
                    color = colors.error,
                    style = typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(spacing.spacing32))

            Text(
                text = stringResource(WelcomeR.string.terms_agreement),
                color = colors.textMuted,
                style = typography.bodySmall,
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
