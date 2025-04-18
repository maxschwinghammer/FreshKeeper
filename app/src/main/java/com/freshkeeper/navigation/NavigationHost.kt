package com.freshkeeper.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.freshkeeper.model.Screen
import com.freshkeeper.screens.aiChat.ChatScreen
import com.freshkeeper.screens.authentication.signIn.EmailSignInScreen
import com.freshkeeper.screens.authentication.signIn.ForgotPasswordScreen
import com.freshkeeper.screens.authentication.signIn.SignInScreen
import com.freshkeeper.screens.authentication.signUp.EmailSignUpScreen
import com.freshkeeper.screens.authentication.signUp.NameInputScreen
import com.freshkeeper.screens.authentication.signUp.SelectProfilePictureScreen
import com.freshkeeper.screens.authentication.signUp.SignUpScreen
import com.freshkeeper.screens.contact.ContactScreen
import com.freshkeeper.screens.help.HelpScreen
import com.freshkeeper.screens.home.HomeScreen
import com.freshkeeper.screens.home.tips.TipsScreen
import com.freshkeeper.screens.household.HouseholdScreen
import com.freshkeeper.screens.householdSettings.HouseholdSettingsScreen
import com.freshkeeper.screens.inventory.InventoryScreen
import com.freshkeeper.screens.landingpage.LandingPageScreen
import com.freshkeeper.screens.notificationSettings.NotificationSettingsScreen
import com.freshkeeper.screens.notifications.NotificationsScreen
import com.freshkeeper.screens.profile.ProfileScreen
import com.freshkeeper.screens.profileSettings.ProfileSettingsScreen
import com.freshkeeper.screens.settings.SettingsScreen
import com.freshkeeper.screens.statistics.StatisticsScreen
import com.freshkeeper.service.account.AccountService

@Suppress("ktlint:standard:function-naming")
@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    accountService: AccountService,
    onLocaleChange: (String) -> Unit,
) {
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
            SignUpScreen(navController)
        }
        composable(Screen.SignIn.route) {
            SignInScreen(navController)
        }
        composable(Screen.EmailSignUp.route) {
            EmailSignUpScreen(navController)
        }
        composable(Screen.EmailSignUp.route) {
            EmailSignUpScreen(navController)
        }
        composable(Screen.EmailSignIn.route) {
            EmailSignInScreen(navController)
        }
        composable(Screen.NameInput.route) {
            NameInputScreen(navController)
        }
        composable(Screen.SelectProfilePicture.route) {
            SelectProfilePictureScreen(navController)
        }
        composable(
            route = "forgotPassword/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType }),
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ForgotPasswordScreen(navController, email = email)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Chat.route) {
            ChatScreen(navController)
        }
        composable(Screen.Household.route) {
            HouseholdScreen(navController)
        }
        composable(Screen.Inventory.route) {
            InventoryScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController, onLocaleChange)
        }
        composable(Screen.Notifications.route) {
            NotificationsScreen(navController)
        }
        composable(Screen.ProfileSettings.route) {
            ProfileSettingsScreen(navController)
        }
        composable(Screen.NotificationSettings.route) {
            NotificationSettingsScreen(navController)
        }
        composable(Screen.HouseholdSettings.route) {
            HouseholdSettingsScreen(navController)
        }
        composable(
            route = "profile/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ProfileScreen(navController, userId)
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen(navController)
        }
        composable(Screen.LandingPage.route) {
            LandingPageScreen(navController)
        }
        composable(Screen.Tips.route) {
            TipsScreen(navController)
        }
        composable(Screen.Contact.route) {
            ContactScreen(navController)
        }
        composable(Screen.Help.route) {
            HelpScreen(navController)
        }
    }
}
