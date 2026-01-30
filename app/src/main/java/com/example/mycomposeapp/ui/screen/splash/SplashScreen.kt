package com.example.mycomposeapp.ui.screen.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.mycomposeapp.R
import com.example.mycomposeapp.ui.theme.GoldGradient
import com.example.mycomposeapp.ui.theme.Spacer
import com.example.mycomposeapp.ui.theme.SplashInitSubtitle
import com.example.mycomposeapp.ui.theme.SplashInitTitle
import com.example.mycomposeapp.ui.theme.SplashOverlayBottom
import com.example.mycomposeapp.ui.theme.SplashOverlayTop
import com.example.mycomposeapp.ui.theme.SplashProgressTrack
import com.example.mycomposeapp.ui.theme.SplashTitleGold
import com.example.mycomposeapp.ui.theme.White
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(SplashContract.Event.OnEnter)
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is SplashContract.SideEffect.NavigateTo -> {
                    navController.navigate(effect.route) {
                        popUpTo("splash") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    SplashContent(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@Composable
fun SplashContent(
    state: SplashContract.State,
    onEvent: (SplashContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.splash),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(SplashOverlayTop, SplashOverlayBottom)
                    )
                )
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

    Box(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .padding(horizontal = Spacer.spacer24, vertical = Spacer.spacer32)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier
                    .padding(top = Spacer.spacer48)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.splash_tagline),
                    style = MaterialTheme.typography.labelSmall.copy(brush = GoldGradient),
                    letterSpacing = 2.sp,
                )

                Spacer(Modifier.height(Spacer.spacer8))

                Text(
                    text = stringResource(R.string.splash_title_guess),
                    style = MaterialTheme.typography.displaySmall.copy(brush = GoldGradient),
                    color = SplashTitleGold,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Text(
                    text = stringResource(R.string.splash_init_title),
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 1.5.sp,
                    color = SplashInitTitle
                )

                Spacer(Modifier.height(Spacer.spacer6))

                Text(
                    text = stringResource(R.string.splash_init_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = SplashInitSubtitle
                )

                Spacer(Modifier.height(Spacer.spacer12))

                GoldGradientProgressBar(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                )

                Spacer(Modifier.height(Spacer.spacer6))

                Text(
                    text = stringResource(R.string.splash_progress_percent, percent),
                    style = MaterialTheme.typography.labelSmall,
                    color = White,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
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
    Canvas(modifier = modifier) {
        val h = size.height
        val r = h / 2f

        drawRoundRect(
            color = SplashProgressTrack,
            size = Size(size.width, h),
            cornerRadius = CornerRadius(r, r)
        )

        val w = size.width * progress.coerceIn(0f, 1f)
        drawRoundRect(
            brush = GoldGradient,
            size = Size(w, h),
            cornerRadius = CornerRadius(r, r)
        )
    }
}

@Preview(
    showBackground = true,
)
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
