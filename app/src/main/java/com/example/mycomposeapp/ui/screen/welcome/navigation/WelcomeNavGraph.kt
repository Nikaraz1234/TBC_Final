package com.example.mycomposeapp.ui.screen.welcome.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mycomposeapp.ui.screen.welcome.WelcomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object WelcomeRoute

fun NavGraphBuilder.welcomeNavGraph(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    composable<WelcomeRoute> {
        WelcomeScreen(
            onLoginClick = onLoginClick,
            onRegisterClick = onRegisterClick,
            onGoogleSignInClick = onGoogleSignInClick,
            onTermsClick = onTermsClick
        )
    }
}
