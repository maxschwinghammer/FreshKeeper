package com.freshkeeper.screens.household

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.model.Household
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.LowerTransition
import com.freshkeeper.screens.UpperTransition
import com.freshkeeper.screens.householdSettings.viewmodel.HouseholdSettingsViewModel
import com.freshkeeper.screens.notifications.viewmodel.NotificationsViewModel
import com.freshkeeper.sheets.AddUserByIdSheet
import com.freshkeeper.sheets.InviteSheet
import com.freshkeeper.sheets.QRCodeSheet
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.TextColor

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun HouseholdScreen(navController: NavHostController) {
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
    val householdSettingsViewModel: HouseholdSettingsViewModel = hiltViewModel()

    val household by householdSettingsViewModel.household.collectAsState(initial = Household())

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val inviteSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val qrCodeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addUserByIdSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val listState = rememberLazyListState()
    val showTransition by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }

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
                Column {
                    Text(
                        text = stringResource(R.string.household),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                        modifier = Modifier.padding(16.dp),
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                        ) {
                            item {
                                MembersSection(
                                    navController,
                                    coroutineScope,
                                    inviteSheetState,
                                    onCreateHouseholdClick = { name, type ->
                                        householdSettingsViewModel.createHousehold(name, type)
                                    },
                                    onJoinHouseholdClick = { householdId ->
                                        householdSettingsViewModel.joinHouseholdById(
                                            householdId,
                                            context,
                                        )
                                    },
                                    onAddProducts = { householdSettingsViewModel.addProducts() },
                                    onDeleteProducts = {
                                        householdSettingsViewModel.deleteProducts()
                                    },
                                )
                            }
                            item { ActivitiesSection() }
                            item { StatisticsSection(navController) }
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

            if (addUserByIdSheetState.isVisible) {
                AddUserByIdSheet(addUserByIdSheetState) { userId ->
                    householdSettingsViewModel.addUserById(
                        userId,
                        context,
                    )
                }
            }
            if (inviteSheetState.isVisible) {
                InviteSheet(qrCodeSheetState, inviteSheetState, addUserByIdSheetState, household.id)
            }
            if (qrCodeSheetState.isVisible) {
                QRCodeSheet(qrCodeSheetState)
            }
        }
    }
}
