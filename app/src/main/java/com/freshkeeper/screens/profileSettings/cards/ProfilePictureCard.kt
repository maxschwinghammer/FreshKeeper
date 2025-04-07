package com.freshkeeper.screens.profileSettings.cards

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.freshkeeper.R
import com.freshkeeper.model.ProfilePicture
import com.freshkeeper.screens.profileSettings.compressImage
import com.freshkeeper.screens.profileSettings.convertBase64ToBitmap
import com.freshkeeper.screens.profileSettings.convertBitmapToBase64
import com.freshkeeper.ui.theme.ComponentStrokeColor
import kotlinx.coroutines.flow.StateFlow

@Suppress("ktlint:standard:function-naming")
@Composable
fun ProfilePictureCard(
    profilePicture: StateFlow<ProfilePicture?>,
    onProfilePictureUpdated: (String) -> Unit,
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val profilePic = profilePicture.collectAsState()
    val imagePainter =
        when {
            profilePic.value?.type == "url" && profilePic.value?.image != null ->
                rememberAsyncImagePainter(profilePic.value!!.image)
            profilePic.value?.image != null -> {
                val bmp = profilePic.value!!.image?.let { convertBase64ToBitmap(it) }
                bmp?.let { BitmapPainter(it.asImageBitmap()) }
            }
            else -> painterResource(R.drawable.profile)
        }

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
        image = imagePainter,
        modifier =
            Modifier
                .card()
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        pickImage.launch("image/*")
    }
}
