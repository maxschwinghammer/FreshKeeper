package com.freshkeeper.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.model.Statistics
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.LowerTransition
import com.freshkeeper.screens.UpperTransition
import com.freshkeeper.screens.household.viewmodel.HouseholdViewModel
import com.freshkeeper.screens.notifications.viewmodel.NotificationsViewModel
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun StatisticsScreen(navController: NavHostController) {
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
    val householdViewModel: HouseholdViewModel = hiltViewModel()

    val listState = rememberLazyListState()
    val showTransition by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }

    val averageNutriments by householdViewModel.averageNutriments.observeAsState()
    val averageNutriScore by householdViewModel.averageNutriScore.observeAsState("N/A")

    val statistics =
        Statistics(
            householdViewModel.totalWaste.observeAsState(0).value,
            householdViewModel.averageWaste.observeAsState(0f).value,
            householdViewModel.daysWithoutWaste.observeAsState(0).value,
            householdViewModel.mostWastedItems.observeAsState(emptyList()).value,
            householdViewModel.usedItemsPercentage.observeAsState(0).value,
            householdViewModel.mostWastedCategory.observeAsState("N/A").value,
            householdViewModel.discardedDates.observeAsState(emptyList()).value,
        )

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
                Column(
                    modifier =
                        Modifier.fillMaxSize().padding(
                            top = 16.dp,
                            start = 16.dp,
                            end = 16.dp,
                        ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.statistics),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        LazyColumn(
                            state = listState,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .clip(RoundedCornerShape(10.dp)),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            item {
                                FoodWasteSummary(statistics)
                            }
                            item {
                                if (statistics.discardedDates.isNotEmpty()) {
                                    FoodWasteBarChart(statistics.discardedDates)
                                }
                            }
                            item {
                                averageNutriments?.let { it1 ->
                                    NutrimentsStatisticsSection(it1, averageNutriScore)
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                        if (showTransition) {
                            UpperTransition()
                            LowerTransition(
                                modifier = Modifier.align(Alignment.BottomCenter),
                            )
                        }
                    }
                }
            }
        }
    }
}
