package com.freshkeeper.service

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import com.freshkeeper.model.Statistics

interface ShareService {
    fun saveBitmapToCache(
        context: Context,
        bitmap: Bitmap,
    ): Uri

    fun captureStatisticsBitmap(
        context: Context,
        content: @Composable () -> Unit,
    ): Bitmap

    fun shareStatistics(
        context: Context,
        statistics: Statistics,
    )
}
