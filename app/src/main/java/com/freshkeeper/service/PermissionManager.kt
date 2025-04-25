package com.freshkeeper.service

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class PermissionManager(
    private val activity: FragmentActivity,
) {
    private val cameraPermissionLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->
            if (!granted) {
                Toast
                    .makeText(
                        activity,
                        "Camera permission denied",
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }

    private val notificationPermissionLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->
            if (!granted) {
                Toast
                    .makeText(
                        activity,
                        "Notification permission denied",
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }

    fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
