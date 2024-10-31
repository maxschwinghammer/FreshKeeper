package com.freshkeeper.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.Grey
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun SignUpScreen(navController: NavHostController) {
    FreshKeeperTheme {
        Scaffold {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
                contentAlignment = Alignment.Center,
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Grey),
                    onClick = { navController.navigate("home") },
                ) {
                    Text("Sign Up", color = TextColor)
                }
            }
        }
    }
}
