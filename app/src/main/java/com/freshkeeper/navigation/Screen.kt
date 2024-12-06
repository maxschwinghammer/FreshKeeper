package com.freshkeeper.navigation

sealed class Screen(
    val route: String,
) {
    data object SignIn : Screen("signIn")

    data object SignUp : Screen("signUp")

    data object EmailSignUp : Screen("emailSignUp")

    data object EmailSignIn : Screen("emailSignIn")

    data object Home : Screen("home")

    data object Inventory : Screen("inventory")

    data object Household : Screen("household")

    data object Settings : Screen("settings")

    data object Notifications : Screen("notifications")

    data object Profile : Screen("profile")

    data object ProfileSettings : Screen("profileSettings")

    data object Statistics : Screen("statistics")

    data object LandingPage : Screen("landingPage")

    data object Tips : Screen("tips")
}
