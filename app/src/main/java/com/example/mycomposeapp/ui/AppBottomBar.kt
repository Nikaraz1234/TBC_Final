package com.example.mycomposeapp.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mycomposeapp.core.ui.R.drawable.app_logo
import com.example.mycomposeapp.core.ui.theme.AppDimensions
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.AppTheme.radius
import com.example.mycomposeapp.core.ui.theme.AppTheme.spacing
import com.example.mycomposeapp.feature.achievements.presentation.navigation.AchievementsRoute
import com.example.mycomposeapp.feature.leaderboard.presentation.navigation.LeaderboardRoute
import com.example.mycomposeapp.feature.main.presentation.navigation.MainRoute
import com.example.mycomposeapp.feature.notification.presentation.navigation.NotificationRoute
import com.example.mycomposeapp.core.ui.R as CoreUiR
import com.example.mycomposeapp.feature.profile.presentation.navigation.ProfileRoute

@Composable
fun AppBottomBar(
    navController: NavHostController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route


    Surface(
        modifier = Modifier
            .navigationBarsPadding()
            .clip(radius.radius12),
        color = AppTheme.colors.glassWhite,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            modifier = Modifier.height(AppDimensions.bottomBarHeight)
        ) {
            BottomBarItem(
                selected = currentRoute == MainRoute::class.qualifiedName,
                painter = painterResource(CoreUiR.drawable.ic_home),
                onClick = {
                    navController.navigate(MainRoute) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            BottomBarItem(
                selected = currentRoute == NotificationRoute::class.qualifiedName,
                painter = painterResource(CoreUiR.drawable.ic_notification),
                onClick = {
                    navController.navigate(NotificationRoute) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            BottomBarItem(
                selected = currentRoute == ProfileRoute::class.qualifiedName,
                painter = painterResource(CoreUiR.drawable.ic_profile),
                onClick = {
                    navController.navigate(ProfileRoute) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            BottomBarItem(
                selected = currentRoute == AchievementsRoute::class.qualifiedName,
                painter = painterResource(CoreUiR.drawable.ic_trophy),
                onClick = {
                    navController.navigate(AchievementsRoute) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            BottomBarItem(
                selected = currentRoute == LeaderboardRoute::class.qualifiedName,
                painter = painterResource(CoreUiR.drawable.ic_leaderboard),
                onClick = {
                    navController.navigate(LeaderboardRoute) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

        }
    }
}

@Composable
private fun RowScope.BottomBarItem(
    selected: Boolean,
    painter: Painter,
    onClick: () -> Unit
) {
    val iconSize = if (selected) AppDimensions.bottomNavIconSelected else AppDimensions.bottomNavIconUnselected

    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )
        },
        modifier = Modifier.padding(bottom = spacing.spacing20),
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = AppTheme.colors.goldenYellow,
            unselectedIconColor = AppTheme.colors.textMuted,
            indicatorColor = AppTheme.colors.glassWhite
        )
    )
}