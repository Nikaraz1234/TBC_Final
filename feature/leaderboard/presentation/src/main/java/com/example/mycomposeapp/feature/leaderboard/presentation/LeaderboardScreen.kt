package com.example.mycomposeapp.feature.leaderboard.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.presentation.common.extensions.CollectWithLifecycle
import com.example.mycomposeapp.core.ui.components.badges.LevelBadge
import com.example.mycomposeapp.core.ui.components.buttons.ButtonMedium
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.theme.AppDimensions
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.core.ui.R as CoreUiR

object LeaderboardTestTags {
    const val TITLE = "lb_title"
    const val CATEGORY_DROPDOWN = "lb_category_dropdown"
    const val MODES_ROW = "lb_modes_row"
    const val TOP_PODIUM = "lb_top_podium"
    const val USER_LIST = "lb_user_list"
    fun mode(mode: String) = "lb_mode_$mode"
    fun userRow(userId: String) = "lb_user_row_$userId"
}

@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel = hiltViewModel(),
    showSnackBar: (String) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.sideEffect.CollectWithLifecycle { effect ->
        when (effect) {
            is LeaderboardContract.SideEffect.ShowSnackBar -> showSnackBar(effect.message)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(LeaderboardContract.Event.LoadLeaderboard)
    }

    LeaderboardContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

/** made internal so androidTest can call it */
@Composable
internal fun LeaderboardContent(
    state: LeaderboardContract.State,
    onEvent: (LeaderboardContract.Event) -> Unit
) {
    val spacing = AppTheme.spacing

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            LeaderboardsTopBar()

            Spacer(modifier = Modifier.height(spacing.spacing16))

            LeaderboardCategories(
                options = state.categories,
                selected = state.selectedCategory,
                onSelectedChange = { onEvent(LeaderboardContract.Event.CategoryChanged(it)) }
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(LeaderboardTestTags.MODES_ROW),
                horizontalArrangement = Arrangement.Center
            ) {
                ModeLayout(
                    modes = state.modes,
                    selectedMode = state.selectedMode,
                    onModeSelected = { mode -> onEvent(LeaderboardContract.Event.ModeChanged(mode)) }
                )
            }

            Spacer(modifier = Modifier.height(spacing.spacing32))

            TopLeaderboard(
                filteredUsers = state.filteredUsers,
                statsKey = state.selectedStatsKey
            )

            Spacer(modifier = Modifier.height(spacing.spacing32))

            Leaderboard(
                users = state.filteredUsers,
                statsKey = state.selectedStatsKey
            )
        }
    }
}

@Composable
private fun LeaderboardsTopBar(
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val typography = AppTheme.typography

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(spacing.spacing56)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = spacing.spacing32),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.leaderboard_title),
                modifier = Modifier.testTag(LeaderboardTestTags.TITLE),
                style = typography.titleLarge.copy(brush = colors.goldTextGradient),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardCategories(
    options: List<LeaderboardFilter>,
    selected: LeaderboardFilter?,
    onSelectedChange: (LeaderboardFilter) -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors

    val onSurface = colors.onSurface
    val surface = colors.surface
    val outline = colors.outline

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (options.isNotEmpty()) expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .padding(horizontal = spacing.spacing32)
                .testTag(LeaderboardTestTags.CATEGORY_DROPDOWN),
            value = selected?.name ?: stringResource(R.string.leaderboard_select_category),
            onValueChange = {},
            readOnly = true,
            enabled = options.isNotEmpty(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = surface.copy(alpha = 0.25f),
                unfocusedContainerColor = surface.copy(alpha = 0.20f),
                disabledContainerColor = surface.copy(alpha = 0.12f),
                focusedTextColor = onSurface,
                unfocusedTextColor = onSurface,
                disabledTextColor = onSurface.copy(alpha = 0.5f),
                focusedIndicatorColor = outline.copy(alpha = 0.0f),
                unfocusedIndicatorColor = outline.copy(alpha = 0.0f)
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name, color = onSurface) },
                    onClick = {
                        onSelectedChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ModeLayout(
    modes: List<String>,
    selectedMode: String,
    onModeSelected: (String) -> Unit
) {
    val spacing = AppTheme.spacing

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(spacing.spacing8),
        contentPadding = PaddingValues(horizontal = spacing.spacing16),
    ) {
        items(modes, key = { it }) { mode ->
            val isSelected = mode == selectedMode
            ButtonMedium(
                text = mode,
                onClick = { onModeSelected(mode) },
                style = if (isSelected) ButtonStyle.Filled else ButtonStyle.Outlined,
                modifier = Modifier
                    .widthIn(min = AppDimensions.leaderboardModeMinWidth)
                    .padding(horizontal = spacing.spacing8)
                    .testTag(LeaderboardTestTags.mode(mode))
            )
        }
    }
}

@Composable
private fun TopLeaderboard(
    filteredUsers: List<User>,
    statsKey: String?
) {
    val top3 = remember(filteredUsers) { filteredUsers.take(3) }
    val ranked = remember(top3) { top3.mapIndexed { index, user -> user to (index + 1) } }
    val podium = remember(ranked) {
        val r1 = ranked.firstOrNull { it.second == 1 }
        val r2 = ranked.firstOrNull { it.second == 2 }
        val r3 = ranked.firstOrNull { it.second == 3 }
        listOfNotNull(r2, r1, r3)
    }

    if (podium.isEmpty()) return

    LazyRow(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .padding(horizontal = AppTheme.spacing.spacing16)
            .fillMaxWidth()
            .testTag(LeaderboardTestTags.TOP_PODIUM)
    ) {
        items(items = podium, key = { (user, _) -> user.userId }) { (user, rank) ->
            LeaderboardAvatarCard(
                user = user,
                rank = rank,
                statsKey = statsKey
            )
        }
    }
}

@Composable
private fun LeaderboardAvatarCard(
    modifier: Modifier = Modifier,
    user: User,
    rank: Int,
    statsKey: String?
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val onSurface = colors.onSurface

    val scale = if (rank == 1) 1.3f else 1f

    Column(
        modifier = modifier
            .height(AppDimensions.leaderboardAvatarCardHeight)
            .scale(scale)
            .testTag(LeaderboardTestTags.userRow(user.userId)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            val medalColor = when (rank) {
                1 -> colors.goldenYellow
                2 -> colors.silver
                3 -> colors.bronze
                else -> colors.outline
            }

            Surface(
                shape = CircleShape,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .size(AppDimensions.leaderboardAvatarSize)
                    .border(
                        width = spacing.spacing4,
                        color = medalColor,
                        shape = CircleShape
                    )
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(CoreUiR.drawable.app_logo),
                    error = painterResource(CoreUiR.drawable.app_logo)
                )
            }

            Surface(
                shape = radius.radius12,
                tonalElevation = 2.dp,
                color = medalColor,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = spacing.spacing10)
            ) {
                Text(
                    text = "#$rank",
                    modifier = Modifier.padding(
                        horizontal = spacing.spacing10,
                        vertical = spacing.spacing4
                    ),
                    color = colors.onSurface
                )
            }
        }

        Spacer(Modifier.height(spacing.spacing16))

        Text(text = user.username, color = onSurface)

        val score = statsKey?.let { user.stats.highScore[it] ?: 0 } ?: user.stats.points
        Text(
            text = stringResource(R.string.leaderboard_score_format, score),
            color = onSurface.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun Leaderboard(
    users: List<User>,
    statsKey: String?
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val onSurface = colors.onSurface

    val restUsers = remember(users) { users.drop(3) }
    if (restUsers.isEmpty()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.spacing24),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.leaderboard_other_rankings),
            color = colors.silver,
            style = AppTheme.typography.titleLarge
        )
        Text(
            text = stringResource(R.string.leaderboard_points),
            color = colors.silver,
            style = AppTheme.typography.titleLarge
        )
    }

    Spacer(modifier = Modifier.height(spacing.spacing8))
    HorizontalDivider(color = onSurface.copy(alpha = 0.12f))
    Spacer(modifier = Modifier.height(spacing.spacing16))

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(LeaderboardTestTags.USER_LIST),
        verticalArrangement = Arrangement.spacedBy(spacing.spacing16)
    ) {
        itemsIndexed(restUsers, key = { _, u -> u.userId }) { index, user ->
            LeaderboardUserCard(
                user = user,
                index = index + 4,
                selectedStatsKey = statsKey
            )
        }
    }
}

@Composable
private fun LeaderboardUserCard(
    user: User,
    index: Int,
    selectedStatsKey: String?
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val onSurface = colors.onSurface
    val muted = onSurface.copy(alpha = 0.75f)

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.spacing32)
            .testTag(LeaderboardTestTags.userRow(user.userId))
    ) {
        Text(
            text = index.toString(),
            color = muted,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.width(spacing.spacing24))

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                shape = CircleShape,
                tonalElevation = 2.dp,
                modifier = Modifier.size(AppDimensions.leaderboardUserAvatarSize)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(CoreUiR.drawable.app_logo),
                    error = painterResource(CoreUiR.drawable.app_logo)
                )
            }

            Spacer(modifier = Modifier.width(spacing.spacing12))

            Column {
                Text(text = user.username, color = onSurface)
                Spacer(modifier = Modifier.height(spacing.spacing4))
                LevelBadge(level = user.stats.level)
            }
        }

        val score = selectedStatsKey?.let { user.stats.highScore[it] ?: 0 } ?: user.stats.points
        Text(
            text = stringResource(R.string.leaderboard_score_format, score),
            color = onSurface.copy(alpha = 0.9f)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LeaderboardContentPreview() {
    MyComposeAppTheme {
        LeaderboardContent(
            state = LeaderboardContract.State(
                isLoading = false,
                users = previewUsers(),
                filteredUsers = previewUsers(),
                modes = listOf("Daily", "Weekly", "All Time"),
            ),
            onEvent = {}
        )
    }
}

private fun previewUsers(): List<User> = listOf(
    User(userId = "1", username = "Nika", photoUrl = ""),
    User(userId = "2", username = "Luka", photoUrl = ""),
    User(userId = "3", username = "Giorgi", photoUrl = ""),
    User(userId = "4", username = "Mariam", photoUrl = ""),
    User(userId = "5", username = "Ana", photoUrl = "")
)