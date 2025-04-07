package com.freshkeeper.service.cropImage

import android.net.Uri
import com.canhub.cropper.CropImageOptions

data class CropImageContractOptions(
    val uri: Uri?,
    val cropImageOptions: CropImageOptions,
)
