package com.freshkeeper.screens.landingpage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.model.Nutriments
import com.freshkeeper.screens.statistics.FoodWasteBarChart
import com.freshkeeper.screens.statistics.NutrimentsStatisticsSection

@Suppress("ktlint:standard:function-naming")
@Composable
fun Story5() {
    val expiredDates =
        listOf(
            System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000,
            System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000,
            System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000,
            System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000,
            System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000,
            System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000,
            System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000,
            System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000,
            System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000,
            System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000,
            System.currentTimeMillis() - 17 * 24 * 60 * 60 * 1000,
            System.currentTimeMillis() - 17 * 24 * 60 * 60 * 1000,
            System.currentTimeMillis() - 19 * 24 * 60 * 60 * 1000,
            System.currentTimeMillis() - 20 * 24 * 60 * 60 * 1000,
            System.currentTimeMillis() - 21 * 24 * 60 * 60 * 1000,
        )
    val averageNutriments =
        Nutriments(
            energyKcal = 250.0,
            fat = 10.5,
            carbohydrates = 30.0,
            sugars = 15.0,
            fiber = 4.5,
            proteins = 8.0,
            salt = 0.5,
        )
    val averageNutriScore = "C"

    StoryTemplate(
        headline = R.string.view_personal_stats,
        content = {
            Column(
                modifier = Modifier.padding(horizontal = 30.dp),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clip(RoundedCornerShape(10.dp)),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        item {
                            FoodWasteBarChart(
                                expiredDates,
                                isStory = true,
                            )
                        }
                        item {
                            NutrimentsStatisticsSection(
                                averageNutriments,
                                averageNutriScore,
                                isStory = true,
                            )
                        }
                    }
                }
            }
        },
    )
}
