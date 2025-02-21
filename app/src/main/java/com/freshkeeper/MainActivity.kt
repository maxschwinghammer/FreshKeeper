package com.freshkeeper

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.freshkeeper.navigation.NavigationHost
import com.freshkeeper.service.AccountServiceImpl
import com.freshkeeper.service.scheduleDailyReminder
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val cameraPermissionRequestCode = 101
    private var navController: NavHostController? = null
    private val currentUserId = Firebase.auth.currentUser?.uid

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) Unit
        }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as
                ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

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

        appUpdateManager = AppUpdateManagerFactory.create(this)

        handleDeepLink(intent)

        FirebaseFirestore
            .getInstance()
            .collection("notificationSettings")
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.first()
                    if (doc.getBoolean("dailyReminders") == true) {
                        doc
                            .getString("dailyNotificationTime")
                            ?.let { scheduleDailyReminder(this, it) }
                    }
                }
            }

        setContent {
            navController = rememberNavController()
            FreshKeeperApp { languageCode ->
                saveLanguageToPreferences(languageCode)
                updateLocale(languageCode)
            }
            if (!isNetworkAvailable()) {
                Toast
                    .makeText(
                        this,
                        "Please connect to the internet",
                        Toast.LENGTH_LONG,
                    ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                )
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

    private fun handleDeepLink(intent: Intent) {
        intent.data?.let { uri ->
            if (uri.host == "freshkeeper.de" && uri.pathSegments.contains("invite")) {
                uri.getQueryParameter("householdId")?.let { householdId ->
                    openHousehold(householdId)
                }
            }
        }
    }

    private fun openHousehold(householdId: String) {
        navController?.navigate("household/$householdId")
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
