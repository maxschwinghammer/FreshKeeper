package com.freshkeeper.screens.profileSettings

import android.content.ClipData.newPlainText
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.freshkeeper.R
import com.freshkeeper.model.ProfilePicture
import com.freshkeeper.model.User
import com.freshkeeper.screens.profileSettings.viewmodel.ProfileSettingsViewModel
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.LightGreyColor
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.StateFlow

@Suppress("ktlint:standard:function-naming")
@Composable
fun AccountCenterCard(
    title: String,
    icon: Any?,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = onCardClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .background(ComponentBackgroundColor)
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) { Text(title, color = TextColor) }
            if (icon != null) {
                when (icon) {
                    is ImageVector ->
                        Icon(
                            imageVector = icon,
                            contentDescription = "Icon",
                            modifier = Modifier.size(24.dp),
                        )
                    is Painter ->
                        Icon(
                            painter = icon,
                            contentDescription = "Icon",
                            modifier = Modifier.size(24.dp),
                        )
                    else -> throw IllegalArgumentException("Unsupported icon type")
                }
            }
        }
    }
}

fun Modifier.card(): Modifier = this.padding(16.dp, 0.dp, 16.dp, 0.dp)

@Suppress("ktlint:standard:function-naming")
@Composable
fun DisplayNameCard(
    displayName: String,
    onUpdateDisplayNameClick: (String) -> Unit,
) {
    var showDisplayNameDialog by remember { mutableStateOf(false) }
    var newDisplayName by remember { mutableStateOf(displayName) }
    val cardTitle = displayName.ifBlank { stringResource(R.string.profile_name) }

    AccountCenterCard(
        "Name: $cardTitle",
        Icons.Filled.Edit,
        Modifier
            .card()
            .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        newDisplayName = displayName
        showDisplayNameDialog = true
    }

    if (showDisplayNameDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.profile_name)) },
            text = {
                Column {
                    TextField(
                        value = newDisplayName,
                        colors =
                            TextFieldDefaults.colors(
                                focusedTextColor = TextColor,
                                unfocusedTextColor = TextColor,
                                focusedContainerColor = GreyColor,
                                unfocusedContainerColor = GreyColor,
                                focusedIndicatorColor = AccentTurquoiseColor,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                        onValueChange = { newDisplayName = it },
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDisplayNameDialog = false },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = GreyColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateDisplayNameClick(newDisplayName)
                        showDisplayNameDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = AccentTurquoiseColor,
                            contentColor = TextColor,
                        ),
                    enabled =
                        newDisplayName.isNotEmpty() &&
                            newDisplayName.all
                                { it.isLetter() || it.isWhitespace() },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.update))
                }
            },
            onDismissRequest = { showDisplayNameDialog = false },
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun EmailCard(
    viewModel: ProfileSettingsViewModel,
    navController: NavController,
    user: User,
) {
    var showChangeEmailDialog by remember { mutableStateOf(false) }
    var newEmail by remember { mutableStateOf("") }
    val cardTitle =
        String.format(
            stringResource(R.string.profile_email),
            user.email,
        )

    val icon: ImageVector? =
        if (user.provider != "google") {
            Icons.Filled.Edit
        } else {
            null
        }

    AccountCenterCard(
        cardTitle,
        icon,
        Modifier
            .card()
            .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        if (user.provider != "google") {
            showChangeEmailDialog = true
        }
    }

    if (showChangeEmailDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.change_email_title), color = TextColor) },
            text = {
                Column {
                    Text(stringResource(R.string.change_email_description), color = TextColor)
                    Spacer(Modifier.padding(8.dp))
                    TextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        placeholder = { Text(user.email) },
                        colors =
                            TextFieldDefaults.colors(
                                focusedTextColor = TextColor,
                                unfocusedTextColor = TextColor,
                                focusedContainerColor = GreyColor,
                                unfocusedContainerColor = GreyColor,
                                focusedIndicatorColor = AccentTurquoiseColor,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                        enabled = user.provider != "google",
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { showChangeEmailDialog = false },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = GreyColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.cancel), color = TextColor)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onChangeEmailClick(newEmail)
                        navController.navigate("signIn")
                        showChangeEmailDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = AccentTurquoiseColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.change_email), color = TextColor)
                }
            },
            onDismissRequest = { showChangeEmailDialog = false },
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Suppress("ktlint:standard:function-naming")
@Composable
fun ProfilePictureCard(
    profilePicture: StateFlow<ProfilePicture?>,
    onProfilePictureUpdated: (String) -> Unit,
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val cropImage =
        rememberLauncherForActivityResult(
            contract = CropImageContract(),
            onResult = { result ->
                if (result.isSuccessful) {
                    val croppedImageUri = result.uriContent
                    croppedImageUri?.let {
                        val compressedBitmap = compressImage(it, context)
                        compressedBitmap?.let { bitmap ->
                            val base64String = convertBitmapToBase64(bitmap)
                            onProfilePictureUpdated(base64String)
                        }
                    }
                }
            },
        )

    val pickImage =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                uri?.let {
                    selectedImageUri = it
                    cropImage.launch(
                        CropImageContractOptions(
                            uri = it,
                            cropImageOptions =
                                CropImageOptions(
                                    guidelines = CropImageView.Guidelines.ON,
                                    outputCompressFormat = Bitmap.CompressFormat.PNG,
                                    fixAspectRatio = true,
                                    aspectRatioX = 1,
                                    aspectRatioY = 1,
                                ),
                        ),
                    )
                }
            },
        )

    AccountCenterCard(
        title = "Profile picture",
        icon = painterResource(R.drawable.profile),
        modifier =
            Modifier
                .card()
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        pickImage.launch("image/*")
    }

    val bitmap =
        remember {
            profilePicture.value?.let {
                it.image?.let { it1 ->
                    convertBase64ToBitmap(
                        it1,
                    )
                }
            }
        }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Profile picture",
            modifier =
                Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, ComponentStrokeColor, CircleShape),
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ResetPasswordCard(
    viewModel: ProfileSettingsViewModel,
    navController: NavController,
) {
    var showResetPasswordDialog by remember { mutableStateOf(false) }

    AccountCenterCard(
        stringResource(R.string.reset_password),
        icon = painterResource(R.drawable.reset_password),
        Modifier.card().border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        showResetPasswordDialog = true
    }

    if (showResetPasswordDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.reset_password_title), color = TextColor) },
            text = { Text(stringResource(R.string.reset_password_description), color = TextColor) },
            dismissButton = {
                Button(
                    onClick = { showResetPasswordDialog = false },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = GreyColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.cancel), color = TextColor)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onResetPasswordClick {
                            navController.navigate("signIn") {
                                popUpTo(0)
                            }
                        }
                        showResetPasswordDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = RedColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.reset), color = TextColor)
                }
            },
            onDismissRequest = { showResetPasswordDialog = false },
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun SignOutCard(onSignOutClick: () -> Unit) {
    var showExitAppDialog by remember { mutableStateOf(false) }

    AccountCenterCard(
        stringResource(R.string.sign_out),
        Icons.AutoMirrored.Filled.ExitToApp,
        Modifier.card().border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        showExitAppDialog = true
    }

    if (showExitAppDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.sign_out_title), color = TextColor) },
            text = { Text(stringResource(R.string.sign_out_description), color = TextColor) },
            dismissButton = {
                Button(
                    onClick = { showExitAppDialog = false },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = GreyColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.cancel), color = TextColor)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSignOutClick()
                        showExitAppDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = RedColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.sign_out), color = TextColor)
                }
            },
            onDismissRequest = { showExitAppDialog = false },
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun RemoveAccountCard(onRemoveAccountClick: () -> Unit) {
    var showRemoveAccDialog by remember { mutableStateOf(false) }

    AccountCenterCard(
        stringResource(R.string.delete_account),
        Icons.Filled.Delete,
        Modifier.card().border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        showRemoveAccDialog = true
    }

    if (showRemoveAccDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.delete_account_title)) },
            text = {
                Text(
                    stringResource(R.string.delete_account_description),
                    color = TextColor,
                )
            },
            dismissButton = {
                Button(
                    onClick = { showRemoveAccDialog = false },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = GreyColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.cancel), color = TextColor)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRemoveAccountClick()
                        showRemoveAccDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = RedColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.delete_account), color = TextColor)
                }
            },
            onDismissRequest = { showRemoveAccDialog = false },
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun BiometricSwitch() {
    val activity = LocalContext.current as? FragmentActivity
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
                                    .setTitle("Biometrische Authentifizierung")
                                    .setSubtitle("Bitte authentifizieren Sie sich, um fortzufahren")
                                    .setNegativeButtonText("Abbrechen")
                                    .build()

                            biometricPrompt.authenticate(promptInfo)
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
                        Text(text = stringResource(R.string.no), color = TextColor)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isBiometricEnabled = true
                            sharedPreferences.edit().putBoolean("biometric_enabled", true).apply()
                            showBiometricDialog = false
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = AccentTurquoiseColor,
                                contentColor = TextColor,
                            ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, ComponentStrokeColor),
                    ) {
                        Text(text = stringResource(R.string.yes), color = TextColor)
                    }
                },
                onDismissRequest = { showBiometricDialog = false },
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun UserIdCard(userId: String) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    AccountCenterCard(
        title = stringResource(R.string.user_id) + ":\n" + userId,
        icon = painterResource(R.drawable.copy),
        modifier =
            Modifier
                .card()
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        val clip = newPlainText("User ID", userId)
        clipboardManager.setPrimaryClip(clip)
    }
}
