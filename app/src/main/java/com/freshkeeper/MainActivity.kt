package com.freshkeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.freshkeeper.navigation.NavigationHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FreshKeeperApp()
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun FreshKeeperApp() {
    val navController = rememberNavController()

    NavigationHost(navController = navController)
}
