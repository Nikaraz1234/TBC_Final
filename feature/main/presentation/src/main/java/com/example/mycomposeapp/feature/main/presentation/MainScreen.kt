package com.example.mycomposeapp.feature.main.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mycomposeapp.core.ui.components.Loader
import com.example.mycomposeapp.core.ui.components.cards.GlassCard
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.main.presentation.components.CategoryCard
import com.example.mycomposeapp.feature.main.presentation.components.DailyChallengeCard
import com.example.mycomposeapp.feature.main.presentation.components.GameModeSection
import com.example.mycomposeapp.feature.main.presentation.components.QuickPlayButton
import com.example.mycomposeapp.feature.main.presentation.components.TipsSection
import com.example.mycomposeapp.feature.main.presentation.components.TodaysGoalsCard
import com.example.mycomposeapp.feature.main.presentation.components.UserTopBar
import com.example.mycomposeapp.feature.main.presentation.R as MainR

@Composable
fun MainScreen(
    onNavigateToGame: (gameModeId: String, categoryType: String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToArchive: () -> Unit = {},
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is MainContract.SideEffect.NavigateToGame -> {
                    onNavigateToGame(effect.gameModeId, effect.categoryType)
                }

                is MainContract.SideEffect.NavigateToProfile -> {
                    onNavigateToProfile()
                }

                is MainContract.SideEffect.ShowSnackbar -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is MainContract.SideEffect.NavigateToWelcome -> {
                    onLogout()
                }

                is MainContract.SideEffect.NavigateToArchive -> {
                    onNavigateToArchive()
                }
            }
        }
    }

    MainContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun MainContent(
    state: MainContract.State,
    onEvent: (MainContract.Event) -> Unit
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val scrollState = rememberScrollState()

    LaunchedEffect(state.selectedCategory) {
        scrollState.scrollTo(0)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = colors.backgroundGradient)
    ) {
        if (state.isLoading) {
            Loader()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .verticalScroll(scrollState)
            ) {
                UserTopBar(
                    user = state.user,
                    onProfileClick = { onEvent(MainContract.Event.OnProfileClicked) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedContent(
                    targetState = state.selectedCategory,
                    transitionSpec = {
                        val isForward = targetState != null
                        val slideDuration = 350
                        val fadeDuration = 175

                        val enterSlide = slideInHorizontally(
                            animationSpec = tween(slideDuration),
                            initialOffsetX = { fullWidth ->
                                if (isForward) fullWidth / 3 else -fullWidth / 3
                            }
                        )
                        val enterFade = fadeIn(animationSpec = tween(slideDuration))

                        val exitSlide = slideOutHorizontally(
                            animationSpec = tween(slideDuration),
                            targetOffsetX = { fullWidth ->
                                if (isForward) -fullWidth / 3 else fullWidth / 3
                            }
                        )
                        val exitFade = fadeOut(animationSpec = tween(fadeDuration))

                        (enterSlide + enterFade) togetherWith (exitSlide + exitFade) using
                                SizeTransform(clip = false)
                    },
                    label = "CategoryTransition"
                ) { selectedCategory ->
                    if (selectedCategory == null) {
                        Column {
                            QuickPlayButton(
                                onClick = { onEvent(MainContract.Event.OnQuickPlayClicked) }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (state.dailyChallenge != null) {
                                DailyChallengeCard(
                                    challenge = state.dailyChallenge,
                                    onClick = { onEvent(MainContract.Event.OnDailyChallengeClicked) }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            ArchiveCard(
                                onClick = { onEvent(MainContract.Event.OnArchiveClicked) }
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            if (state.dailyGoals != null) {
                                TodaysGoalsCard(goalsProgress = state.dailyGoals)
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            CategoriesSection(
                                categories = state.categories,
                                onCategoryClick = { category ->
                                    onEvent(MainContract.Event.OnCategoryClicked(category))
                                }
                            )

                            Spacer(modifier = Modifier.height(24.dp))
                            TipsSection(startIndex = state.tipStartIndex)
                            Spacer(modifier = Modifier.height(24.dp))

                            TextButton(
                                onClick = { onEvent(MainContract.Event.OnLogoutClicked) },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = stringResource(MainR.string.btn_logout),
                                    color = colors.textMuted,
                                    style = typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    } else {
                        GameModeSection(
                            category = selectedCategory,
                            onBackClick = { onEvent(MainContract.Event.OnBackFromGameModes) },
                            onGameModeClick = { gameMode ->
                                onEvent(MainContract.Event.OnGameModeClicked(gameMode))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoriesSection(
    categories: List<com.example.mycomposeapp.feature.main.presentation.model.Category>,
    onCategoryClick: (com.example.mycomposeapp.feature.main.presentation.model.Category) -> Unit
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(MainR.string.categories_header),
            color = colors.textMuted,
            style = typography.labelSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                CategoryCard(
                    category = category,
                    onClick = { onCategoryClick(category) }
                )
            }
        }
    }
}

@Composable
private fun ArchiveCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "\uD83D\uDDC3\uFE0F",
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = stringResource(MainR.string.archive_title),
                    color = colors.textLight,
                    style = typography.titleSmall
                )
                Text(
                    text = stringResource(MainR.string.archive_subtitle),
                    color = colors.textMuted,
                    style = typography.bodySmall
                )
            }
        }
    }
}
