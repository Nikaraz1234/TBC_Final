package com.example.mycomposeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.mycomposeapp.feature.login.presentation.navigation.LoginRoute
import com.example.mycomposeapp.feature.login.presentation.navigation.loginNavGraph
import com.example.mycomposeapp.feature.main.presentation.navigation.MainRoute
import com.example.mycomposeapp.feature.main.presentation.navigation.mainNavGraph
import com.example.mycomposeapp.feature.profile.edit_profile.presentation.navigation.EditProfileRoute
import com.example.mycomposeapp.feature.profile.presentation.navigation.profileNavGraph
import com.example.mycomposeapp.feature.register.presentation.navigation.RegisterRoute
import com.example.mycomposeapp.feature.register.presentation.navigation.registerNavGraph
import com.example.mycomposeapp.feature.splash.presentation.navigation.SplashRoute
import com.example.mycomposeapp.feature.splash.presentation.navigation.splashNavGraph
import com.example.mycomposeapp.feature.welcome.presentation.navigation.WelcomeRoute
import com.example.mycomposeapp.feature.welcome.presentation.navigation.welcomeNavGraph
import com.example.mycomposeapp.feature.game.archive.navigation.ArchiveHubRoute
import com.example.mycomposeapp.feature.game.archive.navigation.EmojiArchiveRoute
import com.example.mycomposeapp.feature.game.archive.navigation.archiveNavGraph
import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.feature.game.presentation.navigation.GameRoute
import com.example.mycomposeapp.feature.game.presentation.navigation.gameNavGraph
import com.example.mycomposeapp.feature.profile.edit_profile.presentation.navigation.editProfileNavGraph
import com.example.mycomposeapp.feature.profile.presentation.navigation.ProfileRoute

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    modifier : Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier
    ) {
        splashNavGraph(
            onGoDashboard = {
                navController.navigate(MainRoute) {
                    popUpTo(SplashRoute) { inclusive = true }
                    launchSingleTop = true
                }
            },
            onGoWelcome = {
                navController.navigate(WelcomeRoute) {
                    popUpTo(SplashRoute) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )

        welcomeNavGraph(
            onNavigateToLogin = { navController.navigate(LoginRoute) },
            onNavigateToRegister = { navController.navigate(RegisterRoute) },
            onNavigateToDashboard = {
                navController.navigate(MainRoute) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )

        loginNavGraph(
            onNavigateToDashboard = {
                navController.navigate(MainRoute) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onNavigateToRegister = {
                navController.navigate(RegisterRoute)
            }
        )

        registerNavGraph(
            onNavigateToDashboard = {
                navController.navigate(MainRoute) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onNavigateToLogin = {
                navController.popBackStack()
            }
        )

        mainNavGraph(
            onNavigateToGame = { gameModeId, categoryType ->
                navController.navigate(GameRoute(gameModeId = gameModeId, categoryType = categoryType))
            },
            onNavigateToProfile = {
                // TODO: Navigate to profile screen
            },
            onLogout = {
                navController.navigate(WelcomeRoute) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onNavigateToArchive = {
                navController.navigate(ArchiveHubRoute)
            }
        )

        gameNavGraph(
            onNavigateBack = { navController.popBackStack() }
        )

        archiveNavGraph(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToEmojiArchive = {
                navController.navigate(EmojiArchiveRoute)
            },
            onNavigateToEmojiGame = { archiveDate ->
                navController.navigate(
                    GameRoute(
                        gameModeId = GameModeIds.EMOJI,
                        categoryType = "movies",
                        archiveDate = archiveDate
                    )
                )
            }
        )

        profileNavGraph(
            onNavigateToEdit = {
                navController.navigate(EditProfileRoute)
            },
            onNavigateToWelcome = {
                navController.navigate(WelcomeRoute) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
        editProfileNavGraph ( onBack = {
            navController.navigate(ProfileRoute)
        })
    }
}
