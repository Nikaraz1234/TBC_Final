package com.example.mycomposeapp.feature.profile.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mycomposeapp.core.ui.R as CoreUiR
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.ui.components.buttons.ButtonMedium
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.theme.AppTheme.colors
import com.example.mycomposeapp.core.ui.theme.AppTheme.radius
import com.example.mycomposeapp.core.ui.theme.AppTheme.spacing
import com.example.mycomposeapp.core.ui.theme.LocalAppColorScheme
import com.example.mycomposeapp.feature.profile.presentation.mapper.toLevelProgressUi
import com.example.mycomposeapp.feature.profile.presentation.model.LevelProgressUi
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onEditClick: () -> Unit,
    onLogout: () -> Unit
){
    val state by viewModel.uiState.collectAsState()

    var showSettingsSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                ProfileContract.SideEffect.ShowSettings -> showSettingsSheet = true
                ProfileContract.SideEffect.GoBack -> Unit
                ProfileContract.SideEffect.GoToEditProfile -> onEditClick()
                ProfileContract.SideEffect.GoToWelcomeScreen -> onLogout()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(ProfileContract.Event.Load)
    }

    ProfileContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = Modifier.fillMaxSize(),
        showSettingsSheet = showSettingsSheet,
        onDismissSettings = { showSettingsSheet = false },
    )
}


@Composable
private fun ProfileContent(
    state: ProfileContract.State,
    onEvent: (ProfileContract.Event) -> Unit,
    modifier: Modifier = Modifier,
    showSettingsSheet: Boolean,
    onDismissSettings: () -> Unit,
){
    val userStats = state.user?.stats
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Image(
            painter = painterResource(CoreUiR.drawable.app_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column {
            ProfileTopBar(
                onBackClick = { onEvent(ProfileContract.Event.OnBackButtonClicked) },
                onSettingsClicked = { onEvent(ProfileContract.Event.OnSettingsClicked) }
            )
            Column(modifier = Modifier.fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
            ) {


                Spacer(modifier= Modifier.height(spacing.spacing32))
                ProfileAvatarCard(
                    photoUrl = state.user?.photoUrl,
                    username = state.user?.username ?: "",
                    level = state.user?.stats?.level ?: 1
                )

                Spacer(modifier= Modifier.height(spacing.spacing32))
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

                GlobalStats(
                    state.user
                )

                Spacer(modifier = Modifier.height(spacing.spacing16))

                ButtonMedium(
                    text = "Edit profile",
                    onClick = { onEvent(ProfileContract.Event.OnEditProfileClicked) },
                    style = ButtonStyle.Filled,
                    enabled = true,
                    modifier = Modifier
                        .padding(horizontal = spacing.spacing16, vertical = spacing.spacing16)
                )

            }
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
    onBackClick: () -> Unit,
    onSettingsClicked: () -> Unit,
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
                .padding(horizontal = spacing.spacing12),
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

            IconButton(onClick = onSettingsClicked) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
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
                text = "Profile",
                style = TextStyle(
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

@Composable
private fun ProfileAvatarCard(
    modifier: Modifier = Modifier,
    photoUrl: String?,
    username: String,
    level: Int
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .fillMaxWidth()
            ,
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = CircleShape,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .size(120.dp)
                    .border(
                        width = 4.dp,
                        color = colors.goldenYellow,
                        shape = CircleShape
                    )
            ) {
                val hasPhoto = !photoUrl.isNullOrBlank()

                if (hasPhoto) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = painterResource(CoreUiR.drawable.app_logo),
                        error = painterResource(CoreUiR.drawable.app_logo)
                    )
                } else {
                    Image(
                        painter = painterResource(CoreUiR.drawable.app_logo),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Surface(
                shape = radius.radius12,
                tonalElevation = 2.dp,
                color = colors.goldenYellow,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (10).dp)
            ) {
                Text(
                    text = "LVL ".plus(level.toString()),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),

                    )
            }
        }

        Spacer(Modifier.height(spacing.spacing32))
        Text(text = username,
            color = colors.white,
            fontSize = 24.sp)
    }
}

@Composable
private fun LevelProgress(
    levelProgress: LevelProgressUi
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.spacing24)
            .clip(radius.radius20)
            .background(colors.glassGradient)
    ) {
        Column(modifier = Modifier.padding(spacing.spacing16)) {
            Text(
                text = "Next Level",
                color = colors.white,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(spacing.spacing8))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = levelProgress.levelText,
                    fontSize = 20.sp,
                    color = colors.white
                )
                Text(
                    text = levelProgress.xpText,
                    color = colors.goldenYellow,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(spacing.spacing16))

            LvlProgressBar(progress = levelProgress.progress)
        }
    }
}
@Composable
private fun LvlProgressBar(
    progress: Float
){
    val colors = LocalAppColorScheme.current

    Canvas(modifier = Modifier
        .height(10.dp)
        .fillMaxWidth()) {
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
private fun GlobalStats(user: User?){
    if(user != null){
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text= "Global Stats",
                color = colors.white,
                fontSize = 20.sp,
                modifier = Modifier.padding(horizontal = spacing.spacing20),
                fontWeight = FontWeight.SemiBold)

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = spacing.spacing24, vertical = spacing.spacing12),
                horizontalArrangement = Arrangement.spacedBy(spacing.spacing16)
            ) {
                RankItem(
                    title = "Points",
                    stat = user.stats.points.toString(),
                    modifier = Modifier.weight(1f)
                )
                RankItem(
                    title = "Games Played",
                    stat = user.stats.gamesPlayed.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = spacing.spacing24, vertical = spacing.spacing12),
                horizontalArrangement = Arrangement.spacedBy(spacing.spacing16)
            ) {
                RankItem(
                    title = "Total Guesses",
                    stat = user.stats.correctAnswers.toString(),
                    modifier = Modifier.weight(1f)
                )
                RankItem(
                    title = "Best Streak",
                    stat = user.stats.currentStreak.values.maxOrNull().toString(),
                    modifier = Modifier.weight(1f),

                    )
            }


        }
    }
}
@Composable
private fun RankItem(
    title: String,
    stat: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(radius.radius28)
            .background(colors.glassGradient)
            .padding(horizontal = spacing.spacing20, vertical = spacing.spacing12),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title.uppercase(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.white.copy(alpha = 0.65f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stat,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colors.white,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheet(
    visible: Boolean,
    onClose: () -> Unit,
    onEvent: (ProfileContract.Event) -> Unit,
    isDarkTheme: Boolean,
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = colors.backgroundDark,
        tonalElevation = 0.dp,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.fillMaxSize()
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
    val configuration = LocalConfiguration.current
    val maxHeight = (configuration.screenHeightDp * maxHeightFraction).dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = maxHeight)
            .background(colors.backgroundDark)
            .padding(horizontal = 16.dp, vertical = 8.dp),

        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Settings",
                fontSize = 18.sp,
                color = colors.white,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close,
                    contentDescription = "Close",
                    tint = colors.white
                )
            }
        }

        HorizontalDivider()
        SheetRow(

            icon = painterResource(CoreUiR.drawable.ic_moon),
            title = "Dark theme",
            onClick = { onEvent(ProfileContract.Event.ToggleDarkTheme) },
            trailing = {
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { onEvent(ProfileContract.Event.ToggleDarkTheme) }
                )
            }
        )
        SheetRow(
            icon = painterResource(CoreUiR.drawable.ic_notification),
            title = "Notifications",
            onClick = { onEvent(ProfileContract.Event.NotificationsClicked) }
        )

        SheetRow(
            icon = painterResource(CoreUiR.drawable.ic_back),
            title = "Logout",
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .background(colors.backgroundDark)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = colors.white
            )
            Spacer(Modifier.width(12.dp))

            Text(
                text = title,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f),
                color = colors.white
            )

            if (trailing != null) trailing()
        }
    }
}
@Preview(name = "Sheet closed", showBackground = true)
@Composable
private fun ProfileContentPreview_SheetClosed() {
    ProfileContent(
        state = ProfileContract.State(),
        onEvent = {},
        showSettingsSheet = false,
        onDismissSettings = {}
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