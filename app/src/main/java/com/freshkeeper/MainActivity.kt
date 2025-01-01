package com.freshkeeper

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.freshkeeper.model.service.AccountServiceImpl
import com.freshkeeper.navigation.NavigationHost
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val cameraPermissionRequestCode = 101

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("FreshKeeperPrefs", Context.MODE_PRIVATE)
        val savedLanguage =
            sharedPreferences.getString(
                "language",
                Locale.getDefault().language,
            )
        updateLocale(savedLanguage ?: Locale.getDefault().language)

        requestPermissions()

        setContent {
            FreshKeeperApp { languageCode ->
                saveLanguageToPreferences(languageCode)
                updateLocale(languageCode)
            }
        }
    }

    private fun saveLanguageToPreferences(languageCode: String) {
        with(sharedPreferences.edit()) {
            putString("language", languageCode)
            apply()
        }
    }

    private fun updateLocale(languageCode: String) {
        val currentLocale = resources.configuration.locales[0]
        val newLocale = Locale(languageCode)

        if (currentLocale.language != newLocale.language) {
            Locale.setDefault(newLocale)
            val config = resources.configuration
            config.setLocale(newLocale)
            resources.updateConfiguration(config, resources.displayMetrics)
            recreate()
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                cameraPermissionRequestCode,
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                102,
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun FreshKeeperApp(onLocaleChange: (String) -> Unit) {
    val navController = rememberNavController()
    val accountService = remember { AccountServiceImpl() }
    NavigationHost(
        navController,
        accountService = accountService,
        onLocaleChange = onLocaleChange,
    )
}
