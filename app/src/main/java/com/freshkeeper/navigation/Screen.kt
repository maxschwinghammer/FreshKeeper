package com.freshkeeper.navigation

sealed class Screen(
    val route: String,
) {
    object Home : Screen("home")

    object Group : Screen("group")

    object Profile : Screen("profile")

    object Settings : Screen("settings")

    object Notifications : Screen("notifications")
}
