package com.example.mycomposeapp.feature.notification.presentation

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.feature.notification.presentation.model.NotificationUi
import com.example.mycomposeapp.feature.notification.presentation.R as NotificationR
import com.example.mycomposeapp.core.ui.R as CoreUiR

object NotificationTestTags {
    const val SCREEN = "notification_screen"
    const val SHEET = "notification_sheet"
    const val OLDER_EMPTY_TEXT = "notification_older_empty"
    fun item(id: String) = "notification_item_$id"
}

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(NotificationContract.Event.ScreenShown)
    }

    NotificationContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun NotificationContent(
    state: NotificationContract.State,
    onEvent: (NotificationContract.Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val spacing = AppTheme.spacing

    val allNotifications = remember(state.newNotifications, state.olderNotifications) {
        state.newNotifications + state.olderNotifications
    }

    var opened by remember { mutableStateOf<NotificationUi?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = modifier
            .testTag(NotificationTestTags.SCREEN)
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.spacing16, vertical = spacing.spacing8),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NotificationTopBar()

            Spacer(modifier = Modifier.height(spacing.spacing16))

            if (state.newNotifications.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.height(2.dp))
                Spacer(modifier = Modifier.height(spacing.spacing16))

                NotificationsColumn(
                    notifications = state.newNotifications,
                    onItemClick = { id -> opened = allNotifications.firstOrNull { it.id == id } }
                )
            }

            Spacer(modifier = Modifier.height(spacing.spacing16))
            HorizontalDivider(modifier = Modifier.height(2.dp))
            Spacer(modifier = Modifier.height(spacing.spacing16))

            if (state.olderNotifications.isNotEmpty()) {
                NotificationsColumn(
                    notifications = state.olderNotifications,
                    onItemClick = { id -> opened = allNotifications.firstOrNull { it.id == id } }
                )
            } else {
                Text(
                    text = stringResource(NotificationR.string.no_older_notifications),
                    modifier = Modifier
                        .testTag(NotificationTestTags.OLDER_EMPTY_TEXT)
                        .padding(horizontal = spacing.spacing16),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                    style = typography.titleMedium
                )
            }
        }
    }

    if (opened != null) {
        val current = opened!!

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                if (!current.isRead) onEvent(NotificationContract.Event.MarkAsRead(current.id))
                opened = null
            },
            containerColor = AppTheme.colors.background,
            tonalElevation = 0.dp,
            scrimColor = colors.transparent
        ) {
            Box(Modifier.testTag(NotificationTestTags.SHEET)) {
                NotificationBottomSheetContent(
                    title = current.title,
                    body = current.body,
                    date = current.date,
                    onClose = {
                        if (!current.isRead) onEvent(NotificationContract.Event.MarkAsRead(current.id))
                        opened = null
                    },
                    onDelete = {
                        onEvent(NotificationContract.Event.Delete(current.id))
                        opened = null
                    }
                )
            }
        }
    }
}

@Composable
private fun NotificationBottomSheetContent(
    title: String,
    body: String,
    date: String,
    onClose: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius

    val onSurface = MaterialTheme.colorScheme.onSurface
    val borderColor = onSurface.copy(alpha = 0.12f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = spacing.spacing16)
            .padding(bottom = spacing.spacing16)
            .clip(radius.radius20)
            .background(colors.glassGradient)
            .border(1.dp, borderColor, radius.radius20)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = typography.titleLarge,
                color = onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    painter = painterResource(id = CoreUiR.drawable.ic_delete),
                    contentDescription = stringResource(NotificationR.string.close),
                    tint = onSurface
                )
            }
        }

        Text(
            text = date,
            style = typography.labelMedium,
            color = onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = body,
            style = typography.bodyLarge,
            color = onSurface.copy(alpha = 0.9f)
        )
    }
}

@Composable
private fun NotificationTopBar() {
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(NotificationR.string.notifications_title),
            style = typography.titleLarge.copy(
                brush = colors.goldTextGradient,
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun NotificationsColumn(
    notifications: List<NotificationUi>,
    onItemClick: (String) -> Unit
) {
    val spacing = AppTheme.spacing

    LazyColumn {
        items(
            items = notifications,
            key = { it.id }
        ) { notification ->
            NotificationItem(
                item = notification,
                onClick = { onItemClick(notification.id) }
            )

            Spacer(Modifier.height(spacing.spacing12))
        }
    }
}

@Composable
private fun NotificationItem(
    item: NotificationUi,
    onClick: (() -> Unit)? = null
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val radius = AppTheme.radius

    val onSurface = MaterialTheme.colorScheme.onSurface
    val borderColor = onSurface.copy(alpha = 0.12f)

    Box(
        modifier = Modifier
            .testTag(NotificationTestTags.item(item.id))
            .fillMaxWidth()
            .clip(radius.radius20)
            .background(colors.glassGradient)
            .border(1.dp, borderColor, radius.radius20)
            .let { m ->
                if (onClick != null) {
                    m.clickable(
                        indication = ripple(bounded = true),
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onClick() }
                } else m
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    modifier = Modifier.weight(1f),
                    color = onSurface,
                    style = typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = item.date,
                    color = onSurface.copy(alpha = 0.75f),
                    style = typography.labelSmall,
                    maxLines = 1
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.body,
                    modifier = Modifier.weight(1f),
                    color = onSurface.copy(alpha = 0.9f),
                    style = typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!item.isRead) {
                    Spacer(Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .border(1.dp, onSurface.copy(alpha = 0.25f), CircleShape)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationItemPreview_Unread() {
    MyComposeAppTheme {
        NotificationItem(
            item = NotificationUi(
                id = "1",
                title = "Daily Streak Reminder",
                body = "Don't forget to play today and keep your streak alive!",
                isRead = false,
                deeplink = "",
                type = "",
                date = "12:30 11/05/2006"
            )
        )
    }
}