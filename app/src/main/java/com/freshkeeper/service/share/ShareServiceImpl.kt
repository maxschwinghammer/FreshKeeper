package com.freshkeeper.service.share

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import com.freshkeeper.R
import com.freshkeeper.model.Statistics
import com.freshkeeper.service.categoryMap
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class ShareServiceImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : ShareService {
        override fun saveBitmapToCache(bitmap: Bitmap): Uri {
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

        override fun captureStatisticsBitmap(content: @Composable () -> Unit): Bitmap {
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

        override fun shareFoodWasteSummary(statistics: Statistics) {
            val bitmap =
                captureStatisticsBitmap {
                    FreshKeeperTheme {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .background(ComponentBackgroundColor, RoundedCornerShape(10.dp))
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
                            if (statistics.totalWaste > 0) {
                                Text(
                                    text =
                                        stringResource(R.string.total_food_waste) + ": " +
                                            statistics.totalWaste + " " +
                                            stringResource(R.string.items),
                                    color = TextColor,
                                    fontSize = 14.sp,
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Image(
                                        modifier = Modifier.size(14.dp),
                                        contentDescription = null,
                                        painter = painterResource(R.drawable.check),
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text =
                                            stringResource(
                                                R.string.no_food_waste,
                                            ),
                                        color = TextColor,
                                        fontSize = 14.sp,
                                    )
                                }
                            }
                            if (statistics.totalWaste > 0) {
                                Text(
                                    text =
                                        stringResource(R.string.average_food_waste) + ": ${
                                            "%.2f"
                                                .format(statistics.averageWaste)
                                        } " +
                                            stringResource(R.string.items),
                                    color = TextColor,
                                    fontSize = 14.sp,
                                )
                            }
                            Text(
                                text =
                                    stringResource(R.string.days_without_waste) + ": " +
                                        statistics.daysWithoutWaste + " " +
                                        stringResource(R.string.days),
                                color = TextColor,
                                fontSize = 14.sp,
                            )
                            if (statistics.totalWaste > 0) {
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
                                                Text(
                                                    text = item.name,
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = ComponentBackgroundColor,
                                                    maxLines = 1,
                                                )
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
                                                Text(
                                                    text = count.toString(),
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = TextColor,
                                                    maxLines = 1,
                                                )
                                            }
                                        }
                                    }
                                }
                                Text(
                                    text =
                                        stringResource(R.string.used_items_percentage) + " " +
                                            statistics.usedItemsPercentage + "%",
                                    color = TextColor,
                                    fontSize = 14.sp,
                                )
                                val categoryRes =
                                    categoryMap[statistics.mostWastedCategory]
                                        ?: R.string.other
                                Text(
                                    text =
                                        stringResource(R.string.most_wasted_category) +
                                            ": " + stringResource(categoryRes),
                                    color = TextColor,
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }
                }
            val uri = saveBitmapToCache(bitmap)
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

        override fun shareFoodWasteBarChart(discardedDates: List<Long>) {
            val today = LocalDate.now()
            val last30Days = (0 until 30).map { today.minusDays(it.toLong()) }.reversed()
            val counts =
                last30Days.map { day ->
                    discardedDates.count { epoch ->
                        Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()).toLocalDate() == day
                    }
                }
            val maxCount = counts.maxOrNull()?.takeIf { it > 0 } ?: 1
            val bitmap =
                captureStatisticsBitmap {
                    FreshKeeperTheme {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .background(ComponentBackgroundColor, RoundedCornerShape(10.dp))
                                    .padding(16.dp),
                        ) {
                            Text(
                                text =
                                    stringResource(R.string.wasted_food_items) +
                                        " - FreshKeeper",
                                color = AccentTurquoiseColor,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 20.dp),
                            )
                            Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                                val leftMargin = 20.dp.toPx()
                                val bottomMargin = 20.dp.toPx()
                                val widthEffective = size.width - leftMargin
                                val heightEffective = size.height - bottomMargin
                                val barWidth = widthEffective / 30f
                                drawLine(
                                    color = WhiteColor,
                                    start = Offset(leftMargin, 0f),
                                    end = Offset(leftMargin, heightEffective),
                                    strokeWidth = 2.dp.toPx(),
                                )
                                drawLine(
                                    color = WhiteColor,
                                    start = Offset(leftMargin, heightEffective),
                                    end = Offset(size.width, heightEffective),
                                    strokeWidth = 2.dp.toPx(),
                                )
                                val textPaint =
                                    android.graphics.Paint().apply {
                                        color = WhiteColor.toArgb()
                                        textSize = 10.sp.toPx()
                                        isAntiAlias = true
                                    }
                                counts.forEachIndexed { index, count ->
                                    val barHeight = (count.toFloat() / maxCount) * heightEffective
                                    drawRect(
                                        color = AccentTurquoiseColor,
                                        topLeft =
                                            Offset(
                                                leftMargin + index * barWidth,
                                                heightEffective - barHeight,
                                            ),
                                        size = Size(barWidth * 0.8f, barHeight),
                                    )
                                }
                                val dateFormatter =
                                    java.time.format.DateTimeFormatter
                                        .ofPattern("dd.MM")
                                (0 until 30).forEach { index ->
                                    if (index % 5 == 0) {
                                        val x = leftMargin + index * barWidth + (barWidth * 0.8f) / 2
                                        val dateLabel = dateFormatter.format(last30Days[index])
                                        drawContext.canvas.nativeCanvas.drawText(
                                            dateLabel,
                                            x,
                                            heightEffective + 15.dp.toPx(),
                                            textPaint,
                                        )
                                    }
                                }
                                drawContext.canvas.nativeCanvas.drawText(
                                    "0",
                                    5.dp.toPx(),
                                    heightEffective,
                                    textPaint,
                                )
                                drawContext.canvas.nativeCanvas.drawText(
                                    "$maxCount",
                                    5.dp.toPx(),
                                    textPaint.textSize,
                                    textPaint,
                                )
                            }
                        }
                    }
                }
            val uri = saveBitmapToCache(bitmap)
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
