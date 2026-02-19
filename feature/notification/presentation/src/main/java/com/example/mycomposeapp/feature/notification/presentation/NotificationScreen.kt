package com.example.mycomposeapp.feature.notification.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.AppTheme.colors
import com.example.mycomposeapp.core.ui.theme.AppTheme.radius
import com.example.mycomposeapp.core.ui.theme.AppTheme.spacing
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.feature.notification.presentation.model.NotificationUi

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationContent(
    state: NotificationContract.State,
    onEvent: (NotificationContract.Event) -> Unit,
    modifier: Modifier = Modifier,
    ) {
    // Combine both lists so we can easily find by id
    val allNotifications = remember(state.newNotifications, state.olderNotifications) {
        state.newNotifications + state.olderNotifications
    }

    // Which notification is currently opened in the sheet
    var opened by remember { androidx.compose.runtime.mutableStateOf<NotificationUi?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = spacing.spacing16),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NotificationTopBar()

        Spacer(modifier = Modifier.height(spacing.spacing16))

        if (state.newNotifications.isNotEmpty()) {
            HorizontalDivider(modifier = Modifier.height(2.dp))
            Spacer(modifier = Modifier.height(spacing.spacing16))

            NotificationsColumn(
                notifications = state.newNotifications,
                onItemClick = { id ->
                    opened = allNotifications.firstOrNull { it.id == id }
                    // optional: keep if you need analytics/navigation logic
                    // onEvent(NotificationContract.Event.NotificationClicked(id))
                }
            )
        }

        Spacer(modifier = Modifier.height(spacing.spacing16))
        HorizontalDivider(modifier = Modifier.height(2.dp))
        Spacer(modifier = Modifier.height(spacing.spacing16))

        if (state.olderNotifications.isNotEmpty()) {
            NotificationsColumn(
                notifications = state.olderNotifications,
                onItemClick = { id ->
                    opened = allNotifications.firstOrNull { it.id == id }
                    // optional
                    // onEvent(NotificationContract.Event.NotificationClicked(id))
                }
            )
        } else {
            Text(
                text = "No Older Notifications",
                modifier = Modifier.padding(horizontal = spacing.spacing16),
                color = colors.textLight,
                style = AppTheme.typography.titleMedium
            )
        }
    }

    // Bottom sheet
    if (opened != null) {
        val current = opened!!

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                // Mark as read ONLY when closed
                if (!current.isRead) onEvent(NotificationContract.Event.MarkAsRead(current.id))
                opened = null
            },
            containerColor = Color.Transparent, // keep your glass look, we draw inside
            tonalElevation = 0.dp
        ) {
            NotificationBottomSheetContent(
                title = current.title,
                body = current.body,
                date = current.date,
                onClose = {
                    if (!current.isRead) onEvent(NotificationContract.Event.MarkAsRead(current.id))
                    opened = null
                }
            )
        }
    }
}

@Composable
private fun NotificationBottomSheetContent(
    title: String,
    body: String,
    date: String,
    onClose: () -> Unit
) {
    val typography = AppTheme.typography

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = spacing.spacing16)
            .padding(bottom = 100.dp)
            .clip(radius.radius20)
            .background(colors.glassGradient)
            .border(1.dp, Color.White.copy(alpha = 0.12f), radius.radius20)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    )  {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = typography.titleLarge,
                color = colors.white,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Close",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        indication = ripple(bounded = true),
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onClose() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                style = typography.labelLarge,
                color = colors.white
            )
        }

        Text(
            text = date,
            style = typography.labelMedium,
            color = colors.white.copy(alpha = 0.7f)
        )

        Text(
            text = body,
            style = typography.bodyLarge,
            color = colors.white.copy(alpha = 0.9f)
        )
    }
}


@Composable
private fun NotificationTopBar() {
    val typography = AppTheme.typography

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Notifications",
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
    val typography = AppTheme.typography

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(radius.radius20)
            .background(colors.glassGradient)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.12f),
                shape = radius.radius20
            )
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
                    color = colors.white,
                    style = typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = item.date,
                    color = colors.white.copy(alpha = 0.75f),
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
                    color = colors.white.copy(alpha = 0.9f),
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
                            .background(Color.White)
                            .border(1.dp, Color.Black.copy(alpha = 0.25f), CircleShape)
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
