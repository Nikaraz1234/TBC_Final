package com.example.mycomposeapp.feature.game.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mycomposeapp.core.ui.components.Loader
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.presentation.components.common.GameErrorView
import com.example.mycomposeapp.feature.game.presentation.components.common.QuestionScreen
import com.example.mycomposeapp.feature.game.presentation.components.common.ResultsView

@Composable
fun GameplayScreen(
    onNavigateBack: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is GameContract.SideEffect.NavigateBack -> onNavigateBack()
                is GameContract.SideEffect.ShowSnackbar -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val colors = AppTheme.colors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = colors.backgroundGradient)
    ) {
        Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        when {
            state.phase == GameContract.GamePhase.Loading && state.errorMessage != null -> {
                GameErrorView(
                    message = state.errorMessage ?: stringResource(R.string.something_went_wrong),
                    onRetry = { viewModel.onEvent(GameContract.Event.OnRetryGame) },
                    onExit = { viewModel.onEvent(GameContract.Event.OnExitGame) }
                )
            }
            state.phase == GameContract.GamePhase.Loading -> {
                Loader()
            }
            state.phase == GameContract.GamePhase.Results -> {
                ResultsView(
                    gameResult = state.gameResult,
                    onPlayAgain = { viewModel.onEvent(GameContract.Event.OnRetryGame) },
                    onExit = { viewModel.onEvent(GameContract.Event.OnExitGame) },
                    isCoverMode = state.isCoverMode,
                    isAchievementMode = state.isAchievementMode
                )
            }
            else -> {
                QuestionScreen(
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
        }
        }
    }
}
