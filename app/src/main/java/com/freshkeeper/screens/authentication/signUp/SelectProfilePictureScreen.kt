package com.freshkeeper.screens.authentication.signUp

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.freshkeeper.R
import com.freshkeeper.screens.profileSettings.compressImage
import com.freshkeeper.screens.profileSettings.convertBitmapToBase64
import com.freshkeeper.screens.profileSettings.viewmodel.ProfileSettingsViewModel
import com.freshkeeper.service.cropImage.CropImageContract
import com.freshkeeper.service.cropImage.CropImageContractOptions
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun SelectProfilePictureScreen(navController: NavHostController) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showError by remember { mutableStateOf(false) }
    val profileSettingsViewModel: ProfileSettingsViewModel = hiltViewModel()
    val context = LocalContext.current
    var skipProfilePicture by remember { mutableStateOf(false) }
    var bitmapState by remember { mutableStateOf<Bitmap?>(null) }

    val imagePainter =
        bitmapState?.asImageBitmap()?.let {
            androidx.compose.ui.graphics.painter
                .BitmapPainter(it)
        } ?: painterResource(id = R.drawable.profile)

    val cropImage =
        rememberLauncherForActivityResult(
            contract = CropImageContract(),
            onResult = { result ->
                if (result.isSuccessful) {
                    val croppedImageUri = result.uriContent
                    croppedImageUri?.let {
                        val compressedBitmap = compressImage(it, context)
                        compressedBitmap?.let { bitmap ->
                            bitmapState = bitmap
                            val base64String = convertBitmapToBase64(bitmap)
                            profileSettingsViewModel.updateProfilePicture(base64String)
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

    FreshKeeperTheme {
        Scaffold {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_transparent),
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.padding(12.dp))
                    Text(
                        text = stringResource(R.string.sign_up_step3),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.padding(12.dp))
                    Text(
                        text = stringResource(R.string.select_profile_picture),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.padding(12.dp))

                    Image(
                        painter = imagePainter,
                        contentDescription = "Profile picture",
                        modifier =
                            Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .clickable { pickImage.launch("image/*") }
                                .border(1.dp, ComponentStrokeColor, CircleShape),
                    )

                    Spacer(Modifier.padding(12.dp))

                    Button(
                        onClick = { pickImage.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentTurquoiseColor),
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
                    ) {
                        Text(
                            text =
                                if (selectedImageUri == null) {
                                    stringResource(R.string.select_picture)
                                } else {
                                    stringResource(R.string.change_picture)
                                },
                            fontSize = 16.sp,
                            color = ComponentBackgroundColor,
                        )
                    }

                    Spacer(Modifier.padding(6.dp))

                    if (showError) {
                        Text(
                            text = stringResource(R.string.select_picture_error),
                            color = RedColor,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        )
                        Spacer(Modifier.padding(6.dp))
                    }

                    if (selectedImageUri == null) {
                        Button(
                            onClick = {
                                skipProfilePicture = true
                                navController.navigate("home") {
                                    launchSingleTop = true
                                }
                            },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = WhiteColor,
                                ),
                            modifier = Modifier.fillMaxWidth().padding(16.dp, 0.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.skip),
                                fontSize = 16.sp,
                                color = ComponentBackgroundColor,
                            )
                        }
                        Spacer(Modifier.padding(6.dp))
                    }

                    if (selectedImageUri != null) {
                        Button(
                            onClick = {
                                if (selectedImageUri == null) {
                                    showError = true
                                } else {
                                    navController.navigate("home") { launchSingleTop = true }
                                }
                            },
                            colors =
                                ButtonDefaults.buttonColors(
                                    disabledContainerColor = WhiteColor,
                                    containerColor = WhiteColor,
                                ),
                            modifier =
                                Modifier.fillMaxWidth().padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 16.dp,
                                ),
                        ) {
                            Text(
                                text = stringResource(R.string.continue_text),
                                fontSize = 16.sp,
                                color = ComponentBackgroundColor,
                            )
                        }
                    }
                }
            }
        }
    }
}
