package com.freshkeeper.screens.landingpage

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.freshkeeper.ui.theme.FreshKeeperTheme

@Suppress("ktlint:standard:function-naming")
@Composable
fun LandingPageScreen(navController: NavHostController) {
    FreshKeeperTheme {
        Story(
            onComplete = { navController.navigate("signUp") },
            navController,
        )
    }
}
