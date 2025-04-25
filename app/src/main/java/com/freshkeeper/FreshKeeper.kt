package com.freshkeeper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.freshkeeper.navigation.NavigationHost
import com.freshkeeper.service.account.AccountServiceImpl

@Suppress("ktlint:standard:function-naming")
@Composable
fun FreshKeeper(onLocaleChange: (String) -> Unit) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val accountService = remember { AccountServiceImpl(context) }
    NavigationHost(
        navController,
        accountService = accountService,
        onLocaleChange = onLocaleChange,
    )
}
