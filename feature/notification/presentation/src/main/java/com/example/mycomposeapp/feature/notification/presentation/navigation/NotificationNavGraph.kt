package com.example.mycomposeapp.feature.notification.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mycomposeapp.feature.notification.presentation.NotificationScreen
import kotlinx.serialization.Serializable

@Serializable
data object NotificationRoute

fun NavGraphBuilder.notificationNavGraph(){
    composable<NotificationRoute> {
        NotificationScreen()
    }
}