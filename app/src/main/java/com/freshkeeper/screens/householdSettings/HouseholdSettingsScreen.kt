package com.freshkeeper.screens.householdSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.freshkeeper.model.User
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.householdSettings.cards.CreateHouseholdCard
import com.freshkeeper.screens.householdSettings.cards.DeleteHouseholdCard
import com.freshkeeper.screens.householdSettings.cards.HouseholdIdCard
import com.freshkeeper.screens.householdSettings.cards.HouseholdNameCard
import com.freshkeeper.screens.householdSettings.cards.InviteCard
import com.freshkeeper.screens.householdSettings.cards.JoinHouseholdCard
import com.freshkeeper.screens.householdSettings.cards.LeaveHouseholdCard
import com.freshkeeper.screens.householdSettings.cards.SelectHouseholdTypeCard
import com.freshkeeper.screens.householdSettings.viewmodel.HouseholdSettingsViewModel
import com.freshkeeper.screens.inventory.viewmodel.InventoryViewModel
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
fun HouseholdSettingsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val viewModel: HouseholdSettingsViewModel = hiltViewModel()
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
    val inventoryViewModel: InventoryViewModel = hiltViewModel()

    val user by viewModel.user.collectAsState(initial = User())
    val household by viewModel.household.collectAsState(initial = Household())
    var selectedHouseholdType by remember { mutableStateOf(household.type) }
    val items by inventoryViewModel.foodItems.observeAsState(emptyList())

    val qrCodeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val inviteSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addUserByIdSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val context = LocalContext.current

    FreshKeeperTheme {
        Scaffold(
            bottomBar = {
                Box(
                    modifier =
                        Modifier
                            .background(BottomNavBackgroundColor)
                            .padding(horizontal = 10.dp),
                ) {
                    BottomNavigationBar(selectedIndex = 3, navController, notificationsViewModel)
                }
            },
        ) { it ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
            ) {
                Text(
                    text = stringResource(R.string.household_settings),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor,
                    modifier = Modifier.padding(16.dp),
                )
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(top = 55.dp),
                ) {
                    item {
                        Column(
                            modifier =
                                modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(15.dp),
                        ) {
                            if (user.householdId == null || household.name.isEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(15.dp),
                                ) {
                                    CreateHouseholdCard(
                                        onCreateHouseholdClick = { name, type ->
                                            viewModel.createHousehold(name, type)
                                        },
                                        onAddProducts = { viewModel.addProducts() },
                                        onDeleteProducts = { viewModel.deleteProducts() },
                                        items,
                                    )
                                    JoinHouseholdCard(onJoinHouseholdClick = { householdId ->
                                        viewModel.joinHouseholdById(
                                            householdId,
                                            context,
                                        )
                                    })
                                }
                            } else {
                                HouseholdNameCard(
                                    { viewModel.updateHouseholdName(it) },
                                    household,
                                    user,
                                )
                                SelectHouseholdTypeCard(
                                    selectedHouseholdType = selectedHouseholdType,
                                    onHouseholdTypeSelected = { type, user ->
                                        selectedHouseholdType = type
                                        viewModel.updateHouseholdType(type, user)
                                    },
                                    onDeleteProducts = { viewModel.deleteProducts() },
                                    onAddProducts = { viewModel.addProducts() },
                                    household,
                                    user,
                                    items,
                                )
                                HouseholdIdCard(household.id)
                                if (household.ownerId == user.id) {
                                    if (household.type != "Single household" &&
                                        (household.type != "Pair" || household.users.size < 2)
                                    ) {
                                        InviteCard(inviteSheetState, household)
                                    }
                                    DeleteHouseholdCard {
                                        viewModel.deleteHousehold()
                                    }
                                } else {
                                    LeaveHouseholdCard {
                                        viewModel.leaveHousehold()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (addUserByIdSheetState.isVisible) {
                AddUserByIdSheet(addUserByIdSheetState) { userId ->
                    viewModel.addUserById(
                        userId,
                        context,
                    )
                }
            }
            if (inviteSheetState.isVisible) {
                InviteSheet(
                    qrCodeSheetState,
                    inviteSheetState,
                    addUserByIdSheetState,
                    household.id,
                )
            }
            if (qrCodeSheetState.isVisible) {
                QRCodeSheet(qrCodeSheetState)
            }
        }
    }
}
