package com.freshkeeper.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import com.freshkeeper.R
import com.freshkeeper.model.Statistics
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ShareServiceImpl
    @Inject
    constructor() : ShareService {
        override fun saveBitmapToCache(
            context: Context,
            bitmap: Bitmap,
        ): Uri {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "image.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                file,
            )
        }

        override fun captureStatisticsBitmap(
            context: Context,
            content: @Composable () -> Unit,
        ): Bitmap {
            val composeView = ComposeView(context)
            composeView.setContent { content() }
            val parent =
                (context as Activity)
                    .window.decorView
                    .findViewById<ViewGroup>(android.R.id.content)
            parent.addView(composeView)
            val width =
                parent.width.takeIf { it > 0 }
                    ?: Resources.getSystem().displayMetrics.widthPixels
            composeView.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            )
            composeView.layout(0, 0, composeView.measuredWidth, composeView.measuredHeight)
            val bitmap = composeView.drawToBitmap()
            parent.removeView(composeView)
            return bitmap
        }

        override fun shareStatistics(
            context: Context,
            statistics: Statistics,
        ) {
            val bitmap =
                captureStatisticsBitmap(context) {
                    FreshKeeperTheme {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .clip(RoundedCornerShape(10.dp))
                                    .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                text =
                                    stringResource(R.string.food_waste_summary) +
                                        " - FreshKeeper",
                                color = AccentTurquoiseColor,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 10.dp),
                            )
                            Text(
                                text =
                                    stringResource(R.string.total_food_waste) + ": " +
                                        statistics.totalWaste + " " +
                                        stringResource(R.string.items),
                                color = TextColor,
                                fontSize = 14.sp,
                            )
                            Text(
                                text =
                                    stringResource(R.string.average_food_waste) + ": ${"%.2f"
                                        .format(statistics.averageWaste)} " +
                                        stringResource(R.string.items),
                                color = TextColor,
                                fontSize = 14.sp,
                            )
                            Text(
                                text =
                                    stringResource(R.string.days_without_waste) + ": " +
                                        statistics.daysWithoutWaste + " " +
                                        stringResource(R.string.days),
                                color = TextColor,
                                fontSize = 14.sp,
                            )
                            if (statistics.mostWastedItems.isNotEmpty()) {
                                Text(
                                    text = stringResource(R.string.most_wasted_food_items) + ":",
                                    color = TextColor,
                                    fontSize = 14.sp,
                                )
                                statistics.mostWastedItems.forEach { (item, count) ->
                                    Row(
                                        modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Box(
                                            modifier =
                                                Modifier
                                                    .clip(
                                                        RoundedCornerShape(
                                                            topStart = 10.dp,
                                                            bottomStart = 10.dp,
                                                        ),
                                                    ).weight(1f)
                                                    .background(WhiteColor)
                                                    .padding(horizontal = 10.dp, vertical = 2.dp),
                                        ) {
                                            if (item != null) {
                                                Text(
                                                    text = item,
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = ComponentBackgroundColor,
                                                    maxLines = 1,
                                                )
                                            }
                                        }
                                        Box(
                                            modifier =
                                                Modifier
                                                    .clip(
                                                        RoundedCornerShape(
                                                            topEnd = 10.dp,
                                                            bottomEnd = 10.dp,
                                                        ),
                                                    ).weight(1f)
                                                    .background(GreyColor)
                                                    .padding(horizontal = 10.dp, vertical = 2.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            if (count != null) {
                                                Text(
                                                    text = count,
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = TextColor,
                                                    maxLines = 1,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Text(
                                text =
                                    stringResource(R.string.waste_reduction) + ": " +
                                        statistics.wasteReduction + "%",
                                color = TextColor,
                                fontSize = 14.sp,
                            )
                            Text(
                                text =
                                    stringResource(R.string.used_items_percentage) + " " +
                                        statistics.usedItemsPercentage + "%",
                                color = TextColor,
                                fontSize = 14.sp,
                            )
                            Text(
                                text =
                                    stringResource(R.string.most_wasted_category) + ": " +
                                        statistics.mostWastedCategory,
                                color = TextColor,
                                fontSize = 14.sp,
                            )
                        }
                    }
                }
            val uri = saveBitmapToCache(context, bitmap)
            val message = context.getString(R.string.share_statistics_message)
            val intent =
                Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, message)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            context.startActivity(Intent.createChooser(intent, "Share Image"))
        }
    }
