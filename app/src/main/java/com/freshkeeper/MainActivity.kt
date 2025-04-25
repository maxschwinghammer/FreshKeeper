package com.freshkeeper

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.freshkeeper.service.AssetFileManager
import com.freshkeeper.service.DeepLinkHandler
import com.freshkeeper.service.NetworkManager
import com.freshkeeper.service.NotificationManager
import com.freshkeeper.service.PermissionManager
import com.freshkeeper.service.UpdateManager
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.initialize
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var navController: NavHostController? = null
    private lateinit var updateManager: UpdateManager
    private val currentUserId = Firebase.auth.currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        Firebase.initialize(context = this)
        if (BuildConfig.DEBUG) {
            Firebase.appCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance(),
            )
        } else {
            Firebase.appCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance(),
            )
        }

        super.onCreate(savedInstanceState)

        val assetFileManager = AssetFileManager(this)
        assetFileManager.copyCsvFromAssets()

        val notificationManager = NotificationManager(this)
        notificationManager.setupNotificationChannel()
        notificationManager.scheduleReminderIfEnabled(
            currentUserId,
            FirebaseFirestore.getInstance(),
        )

        val networkManager = NetworkManager(this)

        sharedPreferences = getSharedPreferences("FreshKeeperPrefs", MODE_PRIVATE)
        val savedLanguage =
            sharedPreferences.getString(
                "language",
                Locale.getDefault().language,
            )
        updateLocale(savedLanguage ?: Locale.getDefault().language)

        val permissionManager = PermissionManager(this)
        permissionManager.requestPermissions()

        updateManager = UpdateManager(this)

        setContent {
            navController = rememberNavController()
            FreshKeeper { languageCode ->
                saveLanguageToPreferences(languageCode)
                updateLocale(languageCode)
            }
            if (!networkManager.isNetworkAvailable()) {
                Toast.makeText(this, "Please connect to the internet", Toast.LENGTH_LONG).show()
            }
            DeepLinkHandler(navController).handleDeepLink(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateManager.checkForUpdates()
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                config.setLocale(newLocale)
            } else {
                @Suppress("DEPRECATION")
                config.locale = newLocale
            }

            @Suppress("DEPRECATION")
            resources.updateConfiguration(config, resources.displayMetrics)

            recreate()
        }
    }
}
