package com.freshkeeper.screens.profileSettings

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import com.freshkeeper.screens.notifications.viewmodel.NotificationsViewModel
import com.freshkeeper.screens.profileSettings.viewmodel.ProfileSettingsViewModel
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.TextColor

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Suppress("ktlint:standard:function-naming")
@Composable
fun ProfileSettingsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: ProfileSettingsViewModel = hiltViewModel(),
) {
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
    val user by viewModel.user.collectAsState(initial = User())

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
                            user.displayName?.let { user ->
                                DisplayNameCard(user) {
                                    viewModel.onUpdateDisplayNameClick(it)
                                }
                            }

                            if (user.isAnonymous) {
                                AccountCenterCard(
                                    stringResource(R.string.authenticate),
                                    Icons.Filled.AccountCircle,
                                    Modifier
                                        .card()
                                        .border(
                                            1.dp,
                                            ComponentStrokeColor,
                                            RoundedCornerShape(10.dp),
                                        ),
                                ) {
                                    navController.navigate("signIn")
                                }
                            } else {
                                EmailCard(viewModel = viewModel, navController = navController, user = user)
                                ProfilePictureCard(
                                    profilePictureBase64 = viewModel.profilePicture.toString(),
                                    onProfilePictureUpdated = { base64 ->
                                        viewModel.updateProfilePicture(base64)
                                    },
                                )
                                UserIdCard(user.id)
                                ResetPasswordCard(viewModel = viewModel, navController = navController)
                                BiometricSwitch()
                                SignOutCard {
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
