package com.freshkeeper

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.freshkeeper.navigation.NavigationHost

class MainActivity : ComponentActivity() {
    @Suppress("ktlint:standard:property-naming")
    private val cameraPermissionRequestCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestCameraPermission()

        setContent {
            FreshKeeperApp()
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraPermissionRequestCode)
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun FreshKeeperApp() {
    val navController = rememberNavController()
    NavigationHost(navController = navController)
}
