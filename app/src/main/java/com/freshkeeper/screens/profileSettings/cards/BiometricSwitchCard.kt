package com.freshkeeper.screens.profileSettings.cards

import android.content.Context
import androidx.activity.compose.LocalActivity
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.CryptoObject
import androidx.compose.foundation.BorderStroke
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import com.freshkeeper.R
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.LightGreyColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("ktlint:standard:function-naming")
@Composable
fun generateSecretKey() {
    val keyGenParameterSpec = KeyGenParameterSpec.Builder(
        "MySecretKey",
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        .setUserAuthenticationRequired(true)
        .setInvalidatedByBiometricEnrollment(true)
        .build()
    val keyGenerator = KeyGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
    )
    keyGenerator.init(keyGenParameterSpec)
    keyGenerator.generateKey()
}

fun getSecretKey(): SecretKey {
    val keyStore = KeyStore.getInstance("AndroidKeyStore")
    keyStore.load(null)
    return keyStore.getKey("MySecretKey", null) as SecretKey
}

fun getCipher(): Cipher {
    return Cipher.getInstance(
        "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
    )
}

fun BiometricSwitchCard() {
    val activity = LocalActivity.current as? FragmentActivity
    Card(
        colors = CardDefaults.cardColors(containerColor = ComponentBackgroundColor),
        modifier = Modifier.card().border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        val context = LocalContext.current
        val sharedPreferences =
            context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

        var isBiometricEnabled by remember {
            mutableStateOf(sharedPreferences.getBoolean("biometric_enabled", false))
        }
        var showBiometricDialog by remember { mutableStateOf(false) }

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val firestore = FirebaseFirestore.getInstance()
        val biometricAuthenticationTitle = stringResource(R.string.biometric_auth_title)
        val biometricAuthenticationSubtitle = stringResource(R.string.biometric_auth_subtitle)
        val cancel = stringResource(R.string.cancel)

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.biometric_auth_title),
                modifier = Modifier.weight(1f),
                color = Color.White,
                fontSize = 16.sp,
            )
            Switch(
                checked = isBiometricEnabled,
                onCheckedChange = { isChecked ->
                    if (!isChecked) {
                        isBiometricEnabled = false
                        sharedPreferences.edit().putBoolean("biometric_enabled", false).apply()

                        user?.let { currentUser ->
                            val userRef =
                                firestore
                                    .collection("users")
                                    .document(currentUser.uid)
                            userRef.update("isBiometricEnabled", false)
                        }
                    } else {
                        activity?.let {
                            val executor = ContextCompat.getMainExecutor(context)
                            val biometricPrompt =
                                BiometricPrompt(
                                    it,
                                    executor,
                                    object : BiometricPrompt.AuthenticationCallback() {
                                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                            val cipher = result.cryptoObject?.cipher
                                            if (cipher != null) {
                                                val encryptedData = cipher.doFinal("SensitiveData".toByteArray())
                                                isBiometricEnabled = true
                                                sharedPreferences
                                                    .edit()
                                                    .putBoolean(
                                                        "biometric_enabled",
                                                        true,
                                                    ).apply()

                                                user?.let { currentUser ->
                                                    val userRef =
                                                        firestore
                                                            .collection("users")
                                                            .document(currentUser.uid)
                                                    userRef.update(
                                                        "isBiometricEnabled",
                                                        true,
                                                    )
                                                }
                                            }
                                        }

                                        override fun onAuthenticationFailed() {
                                            isBiometricEnabled = false
                                            sharedPreferences
                                                .edit()
                                                .putBoolean(
                                                    "biometric_enabled",
                                                    false,
                                                ).apply()
                                        }

                                        override fun onAuthenticationError(
                                            errorCode: Int,
                                            errString: CharSequence,
                                        ) {
                                            isBiometricEnabled = false
                                            sharedPreferences
                                                .edit()
                                                .putBoolean(
                                                    "biometric_enabled",
                                                    false,
                                                ).apply()
                                        }
                                    },
                                )

                            val promptInfo =
                                BiometricPrompt.PromptInfo
                                    .Builder()
                                    .setTitle(biometricAuthenticationTitle)
                                    .setSubtitle(biometricAuthenticationSubtitle)
                                    .setNegativeButtonText(cancel)
                                    .build()

                            generateSecretKey()
                            val cipher = getCipher()
                            val secretKey = getSecretKey()
                            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
                            val cryptoObject = CryptoObject(cipher)
                            biometricPrompt.authenticate(promptInfo, cryptoObject)
                        }
                    }
                },
                colors =
                    SwitchDefaults.colors(
                        checkedBorderColor = ComponentStrokeColor,
                        checkedTrackColor = GreyColor,
                        checkedThumbColor = AccentTurquoiseColor,
                        uncheckedBorderColor = ComponentStrokeColor,
                        uncheckedTrackColor = GreyColor,
                        uncheckedThumbColor = LightGreyColor,
                    ),
                modifier = Modifier.scale(0.9f),
            )
        }

        if (showBiometricDialog) {
            AlertDialog(
                containerColor = ComponentBackgroundColor,
                title = { Text(stringResource(R.string.biometric_auth_title)) },
                text = { Text(stringResource(R.string.biometric_auth_text)) },
                dismissButton = {
                    Button(
                        onClick = {
                            isBiometricEnabled = false
                            sharedPreferences.edit().putBoolean("biometric_enabled", false).apply()
                            showBiometricDialog = false
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = GreyColor,
                                contentColor = TextColor,
                            ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, ComponentStrokeColor),
                    ) {
                        Text(text = stringResource(R.string.no))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isBiometricEnabled = true
                            sharedPreferences.edit { putBoolean("biometric_enabled", true) }
                            showBiometricDialog = false
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = WhiteColor,
                                contentColor = TextColor,
                            ),
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Text(text = stringResource(R.string.yes))
                    }
                },
                onDismissRequest = { showBiometricDialog = false },
            )
        }
    }
}
