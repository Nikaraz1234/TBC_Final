package com.example.mycomposeapp.feature.profile.edit_profile.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mycomposeapp.feature.profile.edit_profile.presentation.EditProfileScreen
import kotlinx.serialization.Serializable

@Serializable data object EditProfileRoute

fun NavGraphBuilder.editProfileNavGraph(
    onBack: () -> Unit,
) {

    composable<EditProfileRoute> {
        EditProfileScreen(
            onBackClick = onBack
        )
    }
}
