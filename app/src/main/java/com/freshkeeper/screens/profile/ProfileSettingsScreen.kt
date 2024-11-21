package com.freshkeeper.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.model.User
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.notifications.NotificationsViewModel
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun ProfileSettingsScreen(
    navController: NavHostController,
    notificationsViewModel: NotificationsViewModel,
    modifier: Modifier = Modifier,
    viewModel: ProfileSettingsScreenViewModel = hiltViewModel(),
) {
    val user by viewModel.user.collectAsState(initial = User())

    FreshKeeperTheme {
        Scaffold(
            bottomBar = {
                Box(
                    modifier =
                        Modifier
                            .background(BottomNavBackgroundColor),
                ) {
                    BottomNavigationBar(selectedIndex = 2, navController, notificationsViewModel)
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
                    text = stringResource(R.string.profile_settings),
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
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    item {
                        Column(
                            modifier =
                                modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            DisplayNameCard(user.displayName) {
                                viewModel.onUpdateDisplayNameClick(it)
                            }

                            Card(
                                modifier =
                                    Modifier
                                        .card()
                                        .border(
                                            1.dp,
                                            ComponentStrokeColor,
                                            shape = CardDefaults.shape,
                                        ),
                            ) {
                                Column(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .background(ComponentBackgroundColor)
                                            .padding(top = 20.dp, start = 20.dp, end = 20.dp),
                                ) {
                                    if (!user.isAnonymous) {
                                        Text(
                                            text =
                                                String.format(
                                                    stringResource(R.string.profile_email),
                                                    user.email,
                                                ),
                                            color = TextColor,
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 16.dp),
                                        )
                                    }
                                }
                            }

                            if (user.isAnonymous) {
                                AccountCenterCard(
                                    stringResource(R.string.authenticate),
                                    Icons.Filled.AccountCircle,
                                    Modifier.card(),
                                ) {
                                    navController.navigate("signIn")
                                }
                            } else {
                                ExitAppCard {
                                    viewModel.onSignOutClick {
                                        navController.navigate("signIn") {
                                            popUpTo(0)
                                        }
                                    }
                                }

                                RemoveAccountCard {
                                    viewModel.onDeleteAccountClick()
                                    navController.navigate("signUp")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
