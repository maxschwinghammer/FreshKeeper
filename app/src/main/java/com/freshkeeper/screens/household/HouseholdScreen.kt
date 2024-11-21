package com.freshkeeper.screens.household

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.household.viewmodel.HouseholdViewModel
import com.freshkeeper.screens.notifications.NotificationsViewModel
import com.freshkeeper.sheets.InviteSheet
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.TextColor

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun HouseholdScreen(
    navController: NavHostController,
    viewModel: HouseholdViewModel = viewModel(),
    notificationsViewModel: NotificationsViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val inviteSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val qrCodeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shareSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val mostWastedItems by viewModel.mostWastedItems.observeAsState(emptyList())

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
                    text = stringResource(R.string.household),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor,
                    modifier = Modifier.padding(16.dp),
                )

                LazyColumn(
                    modifier = Modifier.padding(top = 55.dp, start = 15.dp, end = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        MembersSection(
                            navController,
                            coroutineScope,
                            inviteSheetState,
                            viewModel = viewModel(),
                        )
                    }
                    item {
                        ActivitiesSection(viewModel = viewModel())
                    }
                    item {
                        StatisticsSection(navController, mostWastedItems)
                    }
                }
            }
            if (inviteSheetState.isVisible) {
                InviteSheet(inviteSheetState, qrCodeSheetState, shareSheetState)
            }
        }
    }
}
