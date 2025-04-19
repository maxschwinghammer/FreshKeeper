package com.freshkeeper.screens.profileSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.freshkeeper.screens.LowerTransition
import com.freshkeeper.screens.UpperTransition
import com.freshkeeper.screens.notifications.viewmodel.NotificationsViewModel
import com.freshkeeper.screens.profileSettings.cards.AccountCenterCard
import com.freshkeeper.screens.profileSettings.cards.DisplayNameCard
import com.freshkeeper.screens.profileSettings.cards.DownloadDataCard
import com.freshkeeper.screens.profileSettings.cards.EmailCard
import com.freshkeeper.screens.profileSettings.cards.ProfilePictureCard
import com.freshkeeper.screens.profileSettings.cards.RemoveAccountCard
import com.freshkeeper.screens.profileSettings.cards.ResetPasswordCard
import com.freshkeeper.screens.profileSettings.cards.SignOutCard
import com.freshkeeper.screens.profileSettings.cards.UserIdCard
import com.freshkeeper.screens.profileSettings.cards.card
import com.freshkeeper.screens.profileSettings.viewmodel.ProfileSettingsViewModel
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun ProfileSettingsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val viewModel: ProfileSettingsViewModel = hiltViewModel()
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
    val user by viewModel.user.collectAsState(initial = User())

    val listState = rememberLazyListState()
    val showUpperTransition by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }
    val showLowerTransition by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.size < 9
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
                    text = stringResource(R.string.profile_settings),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor,
                    modifier = Modifier.padding(16.dp),
                )
                LazyColumn(
                    state = listState,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(top = 55.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
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
                            DisplayNameCard(user.displayName) {
                                viewModel.onUpdateDisplayNameClick(it)
                            }

                            if (user.isAnonymous) {
                                AccountCenterCard(
                                    stringResource(R.string.authenticate),
                                    icon = Icons.Filled.AccountCircle,
                                    modifier =
                                        Modifier
                                            .card()
                                            .border(
                                                1.dp,
                                                ComponentStrokeColor,
                                                RoundedCornerShape(10.dp),
                                            ),
                                ) {
                                    navController.navigate("signIn") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            } else {
                                EmailCard(viewModel, navController, user)
                                ProfilePictureCard(
                                    profilePicture = viewModel.profilePicture,
                                    onProfilePictureUpdated = { profilePicture ->
                                        viewModel.updateProfilePicture(profilePicture)
                                    },
                                )
                                UserIdCard(user.id)
                                if (user.provider == "email") {
                                    ResetPasswordCard(viewModel, navController)
                                }
//                                BiometricSwitchCard()
                                DownloadDataCard(user.id, viewModel)
                                SignOutCard {
                                    viewModel.onSignOutClick {
                                        navController.navigate("signIn") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                }
                                RemoveAccountCard {
                                    viewModel.onDeleteAccountClick()
                                    navController.navigate("signUp") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(10.dp)) }
                }
                if (showUpperTransition) {
                    UpperTransition()
                }
                if (showLowerTransition) {
                    LowerTransition(
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                }
            }
        }
    }
}
