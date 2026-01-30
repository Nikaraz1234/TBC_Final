package com.example.mycomposeapp.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object RegisterFirst : Screen("register_first")
    object Dashboard : Screen("dashboard")
}