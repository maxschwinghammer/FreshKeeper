package com.freshkeeper.service.share

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import com.freshkeeper.model.Statistics

interface ShareService {
    fun saveBitmapToCache(bitmap: Bitmap): Uri

    fun captureStatisticsBitmap(content: @Composable () -> Unit): Bitmap

    fun shareFoodWasteSummary(statistics: Statistics)

    fun shareFoodWasteBarChart(discardedDates: List<Long>)
}
