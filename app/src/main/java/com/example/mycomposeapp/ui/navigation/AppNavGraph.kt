package com.example.mycomposeapp.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mycomposeapp.ui.screen.splash.SplashScreen

@Composable
fun AppNavGraph(
    paddingValues: PaddingValues,
    snackBarHostState: SnackbarHostState
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
    }
}