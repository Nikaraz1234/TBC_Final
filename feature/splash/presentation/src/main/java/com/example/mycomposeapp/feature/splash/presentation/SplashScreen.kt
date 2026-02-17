package com.example.mycomposeapp.feature.splash.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import com.example.mycomposeapp.core.ui.R as CoreUiR
import com.example.mycomposeapp.core.ui.theme.AppTheme

@Composable
fun SplashScreen(
    onGoDashboard: () -> Unit,
    onGoWelcome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(SplashContract.Event.OnEnter)
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                SplashContract.SideEffect.GoDashboard -> onGoDashboard()
                SplashContract.SideEffect.GoWelcome -> onGoWelcome()
            }
        }
    }

    SplashContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun SplashContent(
    state: SplashContract.State,
    onEvent: (SplashContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = CoreUiR.drawable.splash),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.splashOverlayGradient)
        )

        SplashForeground(
            progress = state.progress,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun SplashForeground(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val percent = (progress.coerceIn(0f, 1f) * 100).toInt()
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = spacing.spacing24, vertical = spacing.spacing32)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .padding(top = spacing.spacing48)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(CoreUiR.string.splash_tagline),
                    style = typography.labelSmall.merge(
                        TextStyle(
                            brush = colors.goldTextGradient,
                            letterSpacing = 2.sp
                        )
                    )
                )

                Spacer(Modifier.height(spacing.spacing8))

                Text(
                    text = stringResource(CoreUiR.string.splash_title_guess),
                    style = typography.displaySmall.merge(
                        TextStyle(
                            brush = colors.goldTextGradient,
                            fontWeight = FontWeight.Bold
                        )
                    ),
                    color = colors.splashTitleGold
                )
            }

            Column {
                Text(
                    text = stringResource(CoreUiR.string.splash_init_title),
                    style = typography.labelMedium.merge(
                        TextStyle(
                            letterSpacing = 1.5.sp
                        )
                    ),
                    color = colors.splashInitTitle
                )

                Spacer(Modifier.height(spacing.spacing6))

                Text(
                    text = stringResource(CoreUiR.string.splash_init_subtitle),
                    style = typography.bodySmall,
                    color = colors.splashInitSubtitle
                )

                Spacer(Modifier.height(spacing.spacing12))

                GoldGradientProgressBar(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                )

                Spacer(Modifier.height(spacing.spacing6))

                Text(
                    text = stringResource(CoreUiR.string.splash_progress_percent, percent),
                    style = typography.labelSmall,
                    color = colors.white,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        Image(
            painter = painterResource(id = CoreUiR.drawable.app_logo),
            contentDescription = null,
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
fun GoldGradientProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Canvas(modifier = modifier) {
        val h = size.height
        val r = h / 2f

        drawRoundRect(
            color = colors.splashProgressTrack,
            size = Size(size.width, h),
            cornerRadius = CornerRadius(r, r)
        )

        val w = size.width * progress.coerceIn(0f, 1f)
        drawRoundRect(
            brush = colors.goldTextGradient,
            size = Size(w, h),
            cornerRadius = CornerRadius(r, r)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashContentPreview() {
    SplashContent(
        state = SplashContract.State(
            progress = 0.72f,
            isLoading = true
        ),
        onEvent = {}
    )
}
