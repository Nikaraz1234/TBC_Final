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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mycomposeapp.core.ui.components.cards.GlassCard
import com.example.mycomposeapp.core.ui.theme.AppTheme.colors
import com.example.mycomposeapp.core.ui.theme.AppTheme.radius
import com.example.mycomposeapp.core.ui.theme.AppTheme.spacing
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.core.ui.theme.Spacing
import com.example.mycomposeapp.feature.notification.presentation.model.NotificationUi

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel()
){
    val state by viewModel.uiState.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        viewModel.onEvent(NotificationContract.Event.ScreenShown)
    }

    NotificationContent(
        state,
        viewModel::onEvent
    )
}

@Composable
private fun NotificationContent(
    state: NotificationContract.State,
    onEvent: (NotificationContract.Event) -> Unit
){
    Column(modifier = Modifier.fillMaxSize()
        .padding(horizontal = spacing.spacing16)
        .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        NotificationTopBar()
        Spacer(modifier = Modifier.height(spacing.spacing16))

        if(state.newNotifications.isNotEmpty()) {
            HorizontalDivider(modifier = Modifier.height(2.dp))
            Spacer(modifier = Modifier.height(spacing.spacing16))
            NotificationsColumn(
                notifications = state.newNotifications,
                onItemClick = { id ->
                    onEvent(NotificationContract.Event.MarkAsRead(id))
                    onEvent(NotificationContract.Event.NotificationClicked(id))
                }
            )
        }
//        else{
//            Text(text = "No New Notifications",
//                modifier = Modifier.padding(horizontal = spacing.spacing8),
//                color = colors.white,
//                fontSize = 20.sp)
//        }
        Spacer(modifier = Modifier.height(spacing.spacing16))

        HorizontalDivider(modifier = Modifier.height(2.dp))

        Spacer(modifier = Modifier.height(spacing.spacing16))
        if(state.olderNotifications.isNotEmpty()){
            NotificationsColumn(
                notifications = state.olderNotifications,
                onItemClick = { id ->
                    onEvent(NotificationContract.Event.NotificationClicked(id))
                }
            )
        }else{
            Text(text = "No Notifications",
                modifier = Modifier
                    .padding(horizontal = spacing.spacing16),
                color = colors.textLight,
                fontSize = 20.sp)
        }

    }
}

@Composable
private fun NotificationTopBar(){
    Row(horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()){
        Text(text = "Notifications",
            style = TextStyle(
                brush = colors.goldTextGradient,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            ),
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis)
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(radius.radius20)
            .background(colors.glassGradient) // keep your gradient if you like
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
                    fontSize = 16.sp,
                    color = colors.white,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = item.date,
                    fontSize = 12.sp,
                    color = colors.white.copy(alpha = 0.75f),
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
                    fontSize = 14.sp,
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
