package com.example.mycomposeapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.feature.main.presentation.navigation.MainRoute
import com.example.mycomposeapp.feature.notification.presentation.navigation.NotificationRoute
import com.example.mycomposeapp.feature.profile.presentation.navigation.ProfileRoute
import com.example.mycomposeapp.ui.navigation.NavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestNotificationPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()

        var keepSplash = true
        splash.setKeepOnScreenCondition { keepSplash }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestNotificationIfNeeded()


        setContent {
            MyComposeAppTheme {
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    keepSplash = false
                }

                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()

                val showBottomBar = backStackEntry?.destination
                    ?.hierarchy
                    ?.any { destination ->
                        destination.hasRoute<MainRoute>() ||
                                destination.hasRoute<ProfileRoute>() ||
                                destination.hasRoute<NotificationRoute>()
                    } ?: false



                Scaffold(
                    bottomBar = {
                        if (showBottomBar) AppBottomBar(navController)
                    }
                ) { innerPadding ->

                    NavGraph(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }

            }
        }
    }

    private fun requestNotificationIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(permission)
            }
        }
    }
}

