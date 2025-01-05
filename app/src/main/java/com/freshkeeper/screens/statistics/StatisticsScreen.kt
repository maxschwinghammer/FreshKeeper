package com.freshkeeper.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.notifications.NotificationsViewModel
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun StatisticsScreen(
    navController: NavHostController,
    notificationsViewModel: NotificationsViewModel,
    totalFoodWaste: Int,
    averageFoodWastePerDay: Float,
    daysWithNoWaste: Int,
    mostWastedItems: List<Pair<String, String>>,
) {
    FreshKeeperTheme {
        Scaffold(
            bottomBar = {
                Box(
                    modifier =
                        Modifier
                            .background(BottomNavBackgroundColor)
                            .padding(horizontal = 10.dp),
                ) {
                    BottomNavigationBar(selectedIndex = 2, navController, notificationsViewModel)
                }
            },
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
            ) {
                Text(
                    text = stringResource(R.string.statistics),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor,
                    modifier = Modifier.padding(16.dp),
                )
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(top = 55.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item {
                        Text(
                            text =
                                stringResource(R.string.total_food_waste) + ": $totalFoodWaste " +
                                    stringResource(R.string.items),
                            color = TextColor,
                            fontSize = 14.sp,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text =
                                stringResource(R.string.average_food_waste) +
                                    ": ${"%.2f".format(averageFoodWastePerDay)} " +
                                    stringResource(R.string.items),
                            color = TextColor,
                            fontSize = 14.sp,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text =
                                stringResource(R.string.days_without_waste) + ": $daysWithNoWaste " +
                                    stringResource(R.string.days),
                            color = TextColor,
                            fontSize = 14.sp,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.most_wasted_food_items) + ":",
                            color = TextColor,
                            fontSize = 14.sp,
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        mostWastedItems.forEach { (item, count) ->
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
                                            .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                                            .weight(1f)
                                            .background(WhiteColor)
                                            .padding(horizontal = 10.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        text = item,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = ComponentBackgroundColor,
                                        maxLines = 1,
                                    )
                                }

                                Box(
                                    modifier =
                                        Modifier
                                            .clip(RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))
                                            .weight(1f)
                                            .background(GreyColor)
                                            .padding(horizontal = 10.dp, vertical = 2.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = count,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TextColor,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.waste_reduction) + ": 20%",
                            color = TextColor,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }
}
