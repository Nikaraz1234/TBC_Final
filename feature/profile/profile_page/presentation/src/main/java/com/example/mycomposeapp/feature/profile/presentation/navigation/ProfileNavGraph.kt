package com.example.mycomposeapp.feature.profile.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mycomposeapp.feature.profile.presentation.ProfileScreen
import com.example.mycomposeapp.feature.profile.presentation.edit.EditProfileScreen
import kotlinx.serialization.Serializable

@Serializable data object ProfileRoute
@Serializable data object EditProfileRoute

fun NavGraphBuilder.profileNavGraph(
    onNavigateToEdit: () -> Unit,
    onBackFromEdit: () -> Unit,
) {
    composable<ProfileRoute> {
        ProfileScreen(
            onEditClick = onNavigateToEdit
        )
    }

    composable<EditProfileRoute> {
        EditProfileScreen(
            onBackClick = onBackFromEdit
        )
    }
}
