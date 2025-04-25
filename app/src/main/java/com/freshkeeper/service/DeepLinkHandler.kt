package com.freshkeeper.service

import android.content.Intent
import androidx.navigation.NavHostController

class DeepLinkHandler(
    private val navController: NavHostController?,
) {
    fun handleDeepLink(intent: Intent) {
        intent.data?.let { uri ->
            if (uri.host == "freshkeeper.de" && uri.pathSegments.contains("invite")) {
                uri.getQueryParameter("householdId")?.let { householdId ->
                    navController?.navigate("household/$householdId")
                }
            }
        }
    }
}
