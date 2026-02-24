package com.example.mycomposeapp.feature.profile.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.ui.components.avatar.UserAvatar
import com.example.mycomposeapp.core.ui.components.badges.LevelBadge
import com.example.mycomposeapp.core.ui.components.buttons.ButtonMedium
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.theme.AppDimensions
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.profile.presentation.mapper.toLevelProgressUi
import com.example.mycomposeapp.feature.profile.presentation.model.LevelProgressUi
import kotlinx.coroutines.flow.collectLatest
import com.example.mycomposeapp.core.ui.R as CoreUiR
import com.example.mycomposeapp.feature.profile.presentation.R as ProfileR

@Composable
fun ProfileScreen(
    onEditClick: () -> Unit,
    onLogout: () -> Unit,
    showSnackBar: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                ProfileContract.SideEffect.GoToEditProfile -> onEditClick()
                ProfileContract.SideEffect.GoToWelcomeScreen -> onLogout()
                is ProfileContract.SideEffect.ShowSnackBar -> showSnackBar(effect.msg)
            }
        }
    }

    ProfileContent(
        state = state,
        onEvent = viewModel::onEvent,
        showSettingsSheet = state.showSettingsSheet,
        onDismissSettings = { viewModel.onEvent(ProfileContract.Event.OnSettingsDismissed) },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
internal fun ProfileContent(
    state: ProfileContract.State,
    onEvent: (ProfileContract.Event) -> Unit,
    showSettingsSheet: Boolean,
    onDismissSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing

    val userStats = state.user?.stats

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ProfileTopBar(
                onSettingsClicked = { onEvent(ProfileContract.Event.OnSettingsClicked) }
            )

            Spacer(modifier = Modifier.height(spacing.spacing32))

            ProfileAvatarCard(
                photoUrl = state.user?.photoUrl,
                username = state.user?.username.orEmpty(),
                level = state.user?.stats?.level ?: 1
            )

            Spacer(modifier = Modifier.height(spacing.spacing32))

            if (userStats != null) {
                val ui = remember(userStats.totalXp, userStats.level) {
                    toLevelProgressUi(
                        totalXp = userStats.totalXp,
                        level = userStats.level
                    )
                }
                LevelProgress(levelProgress = ui)
            }

            Spacer(modifier = Modifier.height(spacing.spacing16))

            GlobalStats(state.user)

            Spacer(modifier = Modifier.height(spacing.spacing16))

            ButtonMedium(
                text = stringResource(ProfileR.string.edit_profile),
                onClick = { onEvent(ProfileContract.Event.OnEditProfileClicked) },
                style = ButtonStyle.Filled,
                enabled = true,
                modifier = Modifier.padding(
                    horizontal = spacing.spacing16,
                    vertical = spacing.spacing16
                )
            )

            Spacer(modifier = Modifier.height(spacing.spacing24))
        }

        ProfileBottomSheet(
            visible = showSettingsSheet,
            onClose = onDismissSettings,
            onEvent = onEvent,
            isDarkTheme = state.isDarkTheme
        )
    }
}

@Composable
private fun ProfileTopBar(
    onSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val typography = AppTheme.typography

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(AppDimensions.topBarHeight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.spacing12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSettingsClicked) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(ProfileR.string.profile_settings_cd),
                    tint = colors.onBackground
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = spacing.spacing32),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(ProfileR.string.profile_title),
                style = typography.titleLarge.copy(
                    brush = colors.goldTextGradient,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProfileAvatarCard(
    modifier: Modifier = Modifier,
    photoUrl: String?,
    username: String,
    level: Int
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val typography = AppTheme.typography

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(AppDimensions.profileAvatarBorderSize)
                    .border(
                        width = spacing.spacing4,
                        color = colors.goldenYellow,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                UserAvatar(
                    imageUrl = photoUrl,
                    size = AppDimensions.profileAvatarInnerSize
                )
            }

            LevelBadge(
                level = level,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = spacing.spacing10)
            )
        }

        Spacer(modifier = Modifier.height(spacing.spacing32))

        Text(
            text = username,
            color = colors.onBackground,
            style = typography.headlineMedium
        )
    }
}

@Composable
private fun LevelProgress(
    levelProgress: LevelProgressUi
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val typography = AppTheme.typography

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.spacing24)
            .clip(radius.radius20)
            .background(colors.glassGradient)
    ) {
        Column(modifier = Modifier.padding(spacing.spacing16)) {
            Text(
                text = stringResource(ProfileR.string.profile_next_level),
                color = colors.white,
                style = typography.titleMedium
            )

            Spacer(modifier = Modifier.height(spacing.spacing8))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = levelProgress.levelText,
                    color = colors.onSurface,
                    style = typography.headlineSmall
                )
                Text(
                    text = levelProgress.xpText,
                    color = colors.goldenYellow,
                    style = typography.headlineSmall
                )
            }

            Spacer(modifier = Modifier.height(spacing.spacing16))

            LvlProgressBar(progress = levelProgress.progress)
        }
    }
}

@Composable
private fun LvlProgressBar(progress: Float) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing

    Canvas(
        modifier = Modifier
            .height(spacing.spacing10)
            .fillMaxWidth()
    ) {
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

@Composable
private fun GlobalStats(user: User?) {
    if (user == null) return

    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val typography = AppTheme.typography

    val bestStreak = user.stats.bestStreak

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(ProfileR.string.profile_global_stats),
            color = colors.white,
            modifier = Modifier.padding(horizontal = spacing.spacing20),
            style = typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.spacing24, vertical = spacing.spacing12),
            horizontalArrangement = Arrangement.spacedBy(spacing.spacing16)
        ) {
            RankItem(
                title = stringResource(ProfileR.string.profile_stats_points),
                stat = user.stats.points.toString(),
                modifier = Modifier.weight(1f)
            )
            RankItem(
                title = stringResource(ProfileR.string.profile_stats_games_played),
                stat = user.stats.gamesPlayed.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.spacing24, vertical = spacing.spacing12),
            horizontalArrangement = Arrangement.spacedBy(spacing.spacing16)
        ) {
            RankItem(
                title = stringResource(ProfileR.string.profile_stats_total_guesses),
                stat = user.stats.correctAnswers.toString(),
                modifier = Modifier.weight(1f)
            )
            RankItem(
                title = stringResource(ProfileR.string.profile_stats_best_streak),
                stat = bestStreak.toString(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RankItem(
    title: String,
    stat: String,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val typography = AppTheme.typography

    Box(
        modifier = modifier
            .height(AppDimensions.statsCardHeight)
            .clip(radius.radius28)
            .background(colors.glassGradient)
            .padding(horizontal = spacing.spacing20, vertical = spacing.spacing12),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = title.uppercase(),
                color = colors.onSurface.copy(alpha = 0.65f),
                style = typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(spacing.spacing4))

            Text(
                text = stat,
                color = colors.onSurface,
                style = typography.headlineMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileBottomSheet(
    visible: Boolean,
    onClose: () -> Unit,
    onEvent: (ProfileContract.Event) -> Unit,
    isDarkTheme: Boolean,
) {
    if (!visible) return

    val colors = AppTheme.colors

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    LaunchedEffect(Unit) { sheetState.partialExpand() }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = colors.background,
        tonalElevation = 0.dp,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        ProfileBottomSheetContent(
            onClose = onClose,
            onEvent = onEvent,
            isDarkTheme = isDarkTheme,
            maxHeightFraction = 0.40f
        )
    }
}

@Composable
private fun ProfileBottomSheetContent(
    onClose: () -> Unit,
    onEvent: (ProfileContract.Event) -> Unit,
    isDarkTheme: Boolean,
    maxHeightFraction: Float = 0.40f,
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val typography = AppTheme.typography
    val configuration = LocalConfiguration.current
    val maxHeight = (configuration.screenHeightDp * maxHeightFraction).dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = maxHeight)
            .background(colors.backgroundDark)
            .padding(horizontal = spacing.spacing16, vertical = spacing.spacing8),
        verticalArrangement = Arrangement.spacedBy(spacing.spacing8)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(ProfileR.string.profile_settings),
                color = colors.white,
                style = typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(ProfileR.string.close),
                    tint = colors.white
                )
            }
        }

        HorizontalDivider(color = colors.onSurface.copy(alpha = 0.12f))

        SheetRow(
            icon = painterResource(CoreUiR.drawable.ic_delete),
            title = stringResource(ProfileR.string.delete_account),
            onClick = { onEvent(ProfileContract.Event.DeleteUser) }
        )

        SheetRow(
            icon = painterResource(CoreUiR.drawable.ic_back),
            title = stringResource(ProfileR.string.logout),
            onClick = { onEvent(ProfileContract.Event.LogoutClicked) }
        )
    }
}

@Composable
private fun SheetRow(
    icon: Painter,
    title: String,
    onClick: () -> Unit,
    trailing: @Composable (() -> Unit)? = null,
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val typography = AppTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppTheme.radius.radius16)
            .clickable(onClick = onClick)
            .padding(horizontal = spacing.spacing12, vertical = spacing.spacing12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(spacing.spacing20),
            tint = colors.onSurface
        )

        Spacer(Modifier.width(spacing.spacing12))

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = colors.onSurface,
            style = typography.titleMedium
        )

        if (trailing != null) trailing()
    }
}

@Preview(name = "Profile (preview)", showBackground = true, showSystemUi = true)
@Composable
private fun ProfileContentPreview() {
    ProfileContent(
        state = ProfileContract.State(),
        onEvent = {},
        showSettingsSheet = false,
        onDismissSettings = {},
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(showBackground = true)
@Composable
private fun ProfileBottomSheetContentPreview() {
    ProfileBottomSheetContent(
        onClose = {},
        onEvent = {},
        isDarkTheme = true,
        maxHeightFraction = 0.40f
    )
}