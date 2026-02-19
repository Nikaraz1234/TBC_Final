package com.example.mycomposeapp.feature.leaderboard.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.core.domain.model.GameModeIds.statsKey
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.presentation.common.extensions.CollectWithLifecycle
import com.example.mycomposeapp.core.ui.components.buttons.ButtonMedium
import com.example.mycomposeapp.core.ui.components.buttons.ButtonSmall
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.theme.AppTheme.colors
import com.example.mycomposeapp.core.ui.theme.AppTheme.radius
import com.example.mycomposeapp.core.ui.theme.AppTheme.spacing
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import java.time.format.TextStyle
import kotlin.collections.drop
import kotlin.collections.get
import kotlin.collections.sortedByDescending
import com.example.mycomposeapp.core.ui.R as CoreUiR


@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel = hiltViewModel()
){
    val state by viewModel.uiState.collectAsState()

    viewModel.sideEffect.CollectWithLifecycle { effect ->
        when (effect) {
            is LeaderboardContract.SideEffect.ShowSnackBar -> {
                // snackBarHostState.showSnackbar(effect.message)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(LeaderboardContract.Event.LoadLeaderboard)
    }

    LeaderboardContent(
        state =state,
        onEvent = viewModel::onEvent
    )
}
@Composable
private fun LeaderboardContent(
    state: LeaderboardContract.State,
    onEvent: (LeaderboardContract.Event) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()
        .systemBarsPadding()) {

        Column(modifier = Modifier.fillMaxWidth()) {
            LeaderboardsTopBar(
                onBackClick = { },
                onInfoClick = { },
            )
            Spacer(modifier = Modifier.height(spacing.spacing32))
            LeaderboardCategories(
                options = state.categories,
                selected = state.selectedCategory,
                onSelectedChange = { onEvent(LeaderboardContract.Event.CategoryChanged(it)) }

            )
            Spacer(modifier = Modifier.height(spacing.spacing16))
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center){
                ModeLayout(
                    state.modes,
                    state.selectedMode,
                    onModeSelected = { mode ->
                        onEvent(LeaderboardContract.Event.ModeChanged(mode))
                    },
                )
            }


            Spacer(modifier = Modifier.height(spacing.spacing32))
            TopLeaderboard(
                state.filteredUsers,
                state.selectedStatsKey
            )

            Spacer(modifier = Modifier.height(spacing.spacing32))
            Leaderboard(
                state.filteredUsers,
                state.selectedStatsKey
            )
        }
    }

}
@Composable
private fun LeaderboardsTopBar(
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.white
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onInfoClick) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Info",
                    tint = colors.white
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
                text = "Leaderboards",
                style = androidx.compose.ui.text.TextStyle(
                    brush = colors.goldTextGradient,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardCategories(
    options: List<CategoryType>,
    selected: CategoryType?,
    onSelectedChange: (CategoryType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (options.isNotEmpty()) expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .padding(horizontal = spacing.spacing32),
            value = selected?.name ?: "Select category",
            onValueChange = {},
            readOnly = true,
            enabled = options.isNotEmpty(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = colors.backgroundDark,
                unfocusedContainerColor = colors.backgroundDark,
                disabledContainerColor = colors.backgroundDarkEnd,
                focusedTextColor = colors.textLight,
                unfocusedTextColor = colors.textLight,
                focusedIndicatorColor = colors.transparent,
                unfocusedIndicatorColor = colors.transparent
            )
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
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
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),

    ) {
        items(modes) { mode ->
            val isSelected = mode == selectedMode
            ButtonMedium(
                text = mode,
                onClick = { onModeSelected(mode) },
                style = if (isSelected) {
                    ButtonStyle.Filled
                } else {
                    ButtonStyle.Outlined
                },
                modifier = Modifier
                    .widthIn(min = 110.dp)
                    .padding(horizontal = spacing.spacing8)

            )
        }

    }
}
@Composable
private fun TopLeaderboard(
    filteredUsers: List<User>,
    statsKey: String?
){
    val top3 = remember(filteredUsers) {
        filteredUsers.take(3)
    }

    val ranked = remember(top3) {
        top3.mapIndexed { index, user ->
            val rank = index + 1
            user to rank
        }
    }

    val podium = remember(ranked) {
        val rank1 = ranked.firstOrNull { it.second == 1 }
        val rank2 = ranked.firstOrNull { it.second == 2 }
        val rank3 = ranked.firstOrNull { it.second == 3 }
        listOfNotNull(rank2, rank1, rank3)
    }

    if (podium.isEmpty()) return


    LazyRow(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .padding(horizontal = spacing.spacing16)
            .fillMaxWidth()
    ) {
        items(
            items = podium,
            key = { (user, _) -> user.userId }
        ) { (user, rank) ->
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
    val scale = if (rank == 1) 1.3f else 1f
    Column(
        modifier = modifier.height(160.dp)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
            ,
            contentAlignment = Alignment.Center
        ) {
            val medalColor = when (rank) {
                1 -> colors.goldenYellow
                2 -> colors.silver
                3 -> colors.bronze
                else -> MaterialTheme.colorScheme.outline
            }
            Surface(
                shape = CircleShape,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .size(86.dp)
                    .border(
                        width = 4.dp,
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
                    .offset(y = (10).dp)
            ) {
                Text(
                    text = "#".plus(rank.toString()),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),

                    )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = user.username,
            color = colors.white
        )
        val score = statsKey?.let { user.stats.highScore[it] ?: 0 }
            ?: user.stats.points
        Text(text = "$score pts", color = colors.white)
    }
}
@Composable
private fun Leaderboard(
    users: List<User>,
    statsKey: String?
){
    val restUsers = remember(users) {
        users.drop(3)
    }

    if (restUsers.isEmpty()) return

    Row(modifier = Modifier.fillMaxWidth()
        .padding(horizontal = spacing.spacing24),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "Other Rankings",
            color = colors.silver,
            fontSize = 20.sp)

        Text(text = "Points",
            color = colors.silver,
            fontSize = 20.sp)
    }
    Spacer(modifier = Modifier.height(spacing.spacing8))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(spacing.spacing16))
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.spacing16)
    ){
        itemsIndexed(restUsers){ index, user ->
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
){

    Row(horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = spacing.spacing32)
    ){
        Text(text=index.toString(),
            color = colors.silver,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.width(spacing.spacing24))

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ){
            Surface(
                shape = CircleShape,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .size(40.dp)
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
                Text(text = user.username,
                    color = colors.white
                )
                Spacer(modifier = Modifier.height(spacing.spacing4))
                Box(modifier = Modifier
                    .clip(radius.radius8)
                    .background(brush = colors.glassGradient)
                    .padding(spacing.spacing4)
                    .width(40.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(text = "LV. ".plus(user.stats.level.toString()),
                        style = androidx.compose.ui.text.TextStyle(
                            brush = colors.goldTextGradient
                        )
                    )
                }

            }
        }
        val score = selectedStatsKey?.let { user.stats.highScore[it] ?: 0 }
            ?: user.stats.points


        Text(text = "$score pts", color = colors.white)
    }
}


@Preview(
    name = "Leaderboard Card",
    showBackground = true,
    backgroundColor = 0xFF0F1023
)
@Composable
fun LeaderboardAvatarCardPreview() {
    MaterialTheme {
        LeaderboardAvatarCard(
            modifier = Modifier.padding(24.dp),
            user = User(),
            rank = 1,
            statsKey = "1 "
        )
    }
}
private fun previewUsers(): List<User> = listOf(
    User(
        userId = "1",
        username = "Nika",
        photoUrl = ""
    ),
    User(
        userId = "2",
        username = "Luka",
        photoUrl = ""
    ),
    User(
        userId = "3",
        username = "Giorgi",
        photoUrl = ""
    ),
    User(
        userId = "4",
        username = "Mariam",
        photoUrl = ""
    ),
    User(
        userId = "5",
        username = "Ana",
        photoUrl = ""
    )
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LeaderboardContentPreview() {
    MyComposeAppTheme {
        LeaderboardContent(
            state = LeaderboardContract.State(
                isLoading = false,
                users = previewUsers(),
                modes = listOf("Daily", "Weekly", "All Time"),
                //categories = listOf("Movies", "Gold League", "Silver League")
            ),
            onEvent = {}
        )
    }
}