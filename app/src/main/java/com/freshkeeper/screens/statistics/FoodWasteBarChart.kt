package com.freshkeeper.screens.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.R
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.WhiteColor
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Suppress("ktlint:standard:function-naming")
@Composable
fun FoodWasteBarChart(
    expiredDates: List<Long>,
    isStory: Boolean = false,
) {
    val today = LocalDate.now()
    val last30Days = (0 until 30).map { today.minusDays(it.toLong()) }.reversed()
    val counts =
        last30Days.map { day ->
            expiredDates.count { epoch ->
                Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()).toLocalDate() == day
            }
        }
    val maxCount = counts.maxOrNull()?.takeIf { it > 0 } ?: 1

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(ComponentBackgroundColor, RoundedCornerShape(10.dp))
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(bottom = 20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.wasted_food_items),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AccentTurquoiseColor,
                modifier = Modifier.weight(1f),
            )
            if (!isStory) {
                Image(
                    painter = painterResource(R.drawable.share),
                    contentDescription = "Share",
                    modifier =
                        Modifier
                            .size(20.dp),
                )
            }
        }
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
