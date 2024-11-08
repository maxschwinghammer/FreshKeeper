package com.freshkeeper.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.freshkeeper.screens.SignUpScreen
import com.freshkeeper.screens.home.HomeScreen
import com.freshkeeper.screens.home.tips.TipsScreen
import com.freshkeeper.screens.household.HouseholdScreen
import com.freshkeeper.screens.inventory.InventoryScreen
import com.freshkeeper.screens.landingpage.LandingPageScreen
import com.freshkeeper.screens.notifications.NotificationsScreen
import com.freshkeeper.screens.notifications.NotificationsViewModel
import com.freshkeeper.screens.profile.MemberProfileScreen
import com.freshkeeper.screens.settings.SettingsScreen
import com.freshkeeper.screens.statistics.StatisticsScreen

@Suppress("ktlint:standard:function-naming")
@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val notificationsViewModel: NotificationsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.SignUp.route,
        modifier = modifier,
    ) {
        composable(Screen.SignUp.route) { SignUpScreen(navController = navController) }
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                notificationsViewModel = notificationsViewModel,
            )
        }
        composable(Screen.Household.route) {
            HouseholdScreen(
                navController = navController,
                notificationsViewModel = notificationsViewModel,
            )
        }
        composable(Screen.Inventory.route) {
            InventoryScreen(
                navController = navController,
                notificationsViewModel = notificationsViewModel,
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                navController = navController,
                notificationsViewModel = notificationsViewModel,
            )
        }
        composable(Screen.Notifications.route) {
            NotificationsScreen(
                navController = navController,
                notificationsViewModel = notificationsViewModel,
            )
        }
        composable(Screen.MemberProfile.route) {
            MemberProfileScreen(
                navController = navController,
                notificationsViewModel = notificationsViewModel,
            )
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen(
                navController = navController,
                notificationsViewModel = notificationsViewModel,
            )
        }
        composable(Screen.LandingPage.route) {
            LandingPageScreen(
                navController = navController,
            )
        }
        composable(Screen.Tips.route) {
            TipsScreen(
                navController = navController,
                notificationsViewModel = notificationsViewModel,
            )
        }
    }
}
