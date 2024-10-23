package com.freshkeeper.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.freshkeeper.screens.GroupScreen
import com.freshkeeper.screens.HomeScreen
import com.freshkeeper.screens.NotificationsScreen
import com.freshkeeper.screens.ProfileScreen
import com.freshkeeper.screens.SettingsScreen

@Suppress("ktlint:standard:function-naming")
@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
    ) {
        composable(Screen.Home.route) { HomeScreen(navController = navController) }
        composable(Screen.Group.route) { GroupScreen(navController = navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController = navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController = navController) }
        composable(Screen.Notifications.route) { NotificationsScreen(navController = navController) }
    }
}
