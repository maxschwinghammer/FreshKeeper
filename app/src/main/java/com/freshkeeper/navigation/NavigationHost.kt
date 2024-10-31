package com.freshkeeper.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.freshkeeper.screens.HouseholdScreen
import com.freshkeeper.screens.NotificationsScreen
import com.freshkeeper.screens.SettingsScreen
import com.freshkeeper.screens.SignUpScreen
import com.freshkeeper.screens.home.HomeScreen
import com.freshkeeper.screens.inventory.InventoryScreen

@Suppress("ktlint:standard:function-naming")
@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.SignUp.route,
        modifier = modifier,
    ) {
        composable(Screen.SignUp.route) { SignUpScreen(navController = navController) }
        composable(Screen.Home.route) { HomeScreen(navController = navController) }
        composable(Screen.Household.route) { HouseholdScreen(navController = navController) }
        composable(Screen.Inventory.route) { InventoryScreen(navController = navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController = navController) }
        composable(Screen.Notifications.route) { NotificationsScreen(navController = navController) }
    }
}
