package com.example.mycomposeapp.feature.profile.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mycomposeapp.feature.profile.presentation.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable data object ProfileRoute

fun NavGraphBuilder.profileNavGraph(
    onNavigateToEdit: () -> Unit,
    onNavigateToWelcome: () -> Unit
) {
    composable<ProfileRoute> {
        ProfileScreen(
            onEditClick = onNavigateToEdit,
            onLogout = onNavigateToWelcome
        )
    }
}
