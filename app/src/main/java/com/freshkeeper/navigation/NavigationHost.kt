package com.freshkeeper.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.freshkeeper.model.service.AccountService
import com.freshkeeper.screens.authentication.signIn.EmailSignInScreen
import com.freshkeeper.screens.authentication.signIn.ForgotPasswordScreen
import com.freshkeeper.screens.authentication.signIn.SignInScreen
import com.freshkeeper.screens.authentication.signUp.EmailSignUpScreen
import com.freshkeeper.screens.authentication.signUp.SignUpScreen
import com.freshkeeper.screens.home.HomeScreen
import com.freshkeeper.screens.home.tips.TipsScreen
import com.freshkeeper.screens.household.HouseholdScreen
import com.freshkeeper.screens.inventory.InventoryScreen
import com.freshkeeper.screens.landingpage.LandingPageScreen
import com.freshkeeper.screens.notificationSettings.NotificationSettingsScreen
import com.freshkeeper.screens.notifications.NotificationsScreen
import com.freshkeeper.screens.notifications.NotificationsViewModel
import com.freshkeeper.screens.profile.ProfileScreen
import com.freshkeeper.screens.profileSettings.ProfileSettingsScreen
import com.freshkeeper.screens.settings.SettingsScreen
import com.freshkeeper.screens.statistics.StatisticsScreen

@Suppress("ktlint:standard:function-naming")
@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    accountService: AccountService,
    onLocaleChange: (String) -> Unit,
) {
    val notificationsViewModel: NotificationsViewModel = viewModel()
    val startDestination =
        when {
            !accountService.hasUser() -> Screen.LandingPage.route
            accountService.getUserProfile().isAnonymous -> Screen.LandingPage.route
            else -> Screen.Home.route
        }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Screen.SignUp.route) {
            SignUpScreen(
                navController = navController,
            )
        }
        composable(Screen.SignIn.route) {
            SignInScreen(
                navController = navController,
            )
        }
        composable(Screen.EmailSignUp.route) {
            EmailSignUpScreen(
                navController = navController,
            )
        }
        composable(Screen.EmailSignIn.route) {
            EmailSignInScreen(
                navController = navController,
            )
        }
        composable(
            route = "forgotPassword/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType }),
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ForgotPasswordScreen(navController = navController, email = email)
        }
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
                onLocaleChange = onLocaleChange,
            )
        }
        composable(Screen.Notifications.route) {
            NotificationsScreen(
                navController = navController,
                notificationsViewModel = notificationsViewModel,
            )
        }
        composable(Screen.ProfileSettings.route) {
            ProfileSettingsScreen(
                navController = navController,
                notificationsViewModel = notificationsViewModel,
            )
        }
        composable(Screen.NotificationSettings.route) {
            NotificationSettingsScreen(
                navController = navController,
                notificationsViewModel = notificationsViewModel,
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
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
