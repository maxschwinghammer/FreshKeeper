package com.freshkeeper.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.FreshKeeperTheme

@Suppress("ktlint:standard:function-naming")
@Composable
fun GroupScreen(navController: NavHostController) {
    FreshKeeperTheme {
        Scaffold(
            bottomBar = {
                Box(
                    modifier =
                        Modifier
                            .background(BottomNavBackgroundColor)
                            .padding(horizontal = 10.dp),
                ) {
                    BottomNavigationBar(selectedIndex = 1, navController)
                }
            },
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Group Screen",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}
