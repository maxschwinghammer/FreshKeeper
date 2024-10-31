package com.freshkeeper.navigation

sealed class Screen(
    val route: String,
) {
    data object SignUp : Screen("signUp")

    data object Home : Screen("home")

    data object Inventory : Screen("inventory")

    data object Household : Screen("household")

    data object Settings : Screen("settings")

    data object Notifications : Screen("notifications")
}
