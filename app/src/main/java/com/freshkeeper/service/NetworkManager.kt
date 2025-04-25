package com.freshkeeper.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat

class NetworkManager(
    private val context: Context,
) {
    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            ContextCompat.getSystemService(
                context,
                ConnectivityManager::class.java,
            )
                ?: return false
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }
}
