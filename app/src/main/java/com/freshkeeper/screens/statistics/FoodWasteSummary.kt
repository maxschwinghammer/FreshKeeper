package com.freshkeeper.screens.statistics

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.R
import com.freshkeeper.model.Statistics
import com.freshkeeper.service.categoryMap
import com.freshkeeper.service.share.ShareService
import com.freshkeeper.service.share.ShareServiceImpl
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun FoodWasteSummary(statistics: Statistics) {
    val context = LocalContext.current
    val shareService: ShareService = ShareServiceImpl(context)

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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.food_waste_summary),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AccentTurquoiseColor,
                modifier = Modifier.weight(1f).padding(bottom = 8.dp),
            )
            Image(
                painter = painterResource(R.drawable.share),
                contentDescription = "Share",
                modifier =
                    Modifier
                        .size(20.dp)
                        .clickable {
                            shareService.shareFoodWasteSummary(statistics)
                        },
            )
        }
        Text(
            text =
                stringResource(R.string.total_food_waste) + ": " +
                    statistics.totalWaste + " " +
                    stringResource(R.string.items),
            color = TextColor,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text =
                stringResource(R.string.average_food_waste) +
                    ": ${"%.2f".format(statistics.averageWaste)} " +
                    stringResource(R.string.items),
            color = TextColor,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text =
                stringResource(R.string.days_without_waste) + ": " +
                    statistics.daysWithoutWaste + " " +
                    stringResource(R.string.days),
            color = TextColor,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))

        if (statistics.mostWastedItems.isNotEmpty()) {
            Text(
                text = stringResource(R.string.most_wasted_food_items) + ":",
                color = TextColor,
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))

            statistics.mostWastedItems.forEach { (item, count) ->
                Row(
                    modifier =
                        Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth(),
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
                                .padding(
                                    horizontal = 10.dp,
                                    vertical = 2.dp,
                                ),
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
                                .padding(
                                    horizontal = 10.dp,
                                    vertical = 2.dp,
                                ),
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
            Spacer(modifier = Modifier.height(4.dp))
        }
        Text(
            text =
                stringResource(R.string.used_items_percentage) +
                    " " + statistics.usedItemsPercentage + " %",
            color = TextColor,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
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
