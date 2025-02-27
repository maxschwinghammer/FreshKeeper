package com.freshkeeper.screens.notificationSettings

import android.app.NotificationManager
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.LowerTransition
import com.freshkeeper.screens.UpperTransition
import com.freshkeeper.screens.notificationSettings.cards.NotificationPermissionCard
import com.freshkeeper.screens.notificationSettings.cards.SelectDailyNotificationTimeCard
import com.freshkeeper.screens.notificationSettings.cards.UpdateTimeBeforeExpirationCard
import com.freshkeeper.screens.notificationSettings.viewmodel.NotificationSettingsViewModel
import com.freshkeeper.screens.notifications.viewmodel.NotificationsViewModel
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.TextColor
import java.time.LocalTime

@Suppress("ktlint:standard:function-naming")
@Composable
fun NotificationSettingsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val viewModel: NotificationSettingsViewModel = hiltViewModel()
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
    val context = LocalContext.current

    val notificationSettings by viewModel.notificationSettings.collectAsState()

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val isNotificationEnabled = notificationManager.areNotificationsEnabled()

    val listState = rememberLazyListState()
    val showUpperTransition by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }
    val showLowerTransition by remember {
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
                    BottomNavigationBar(selectedIndex = 3, navController, notificationsViewModel)
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
                    text = stringResource(R.string.notification_settings),
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
                            NotificationPermissionCard(context, isNotificationEnabled)
                            SelectDailyNotificationTimeCard(
                                LocalTime.parse(
                                    notificationSettings?.dailyNotificationTime
                                        ?: LocalTime.of(12, 0).toString(),
                                ),
                                onTimeSelected = { time ->
                                    viewModel.updateDailyNotificationTime(time)
                                },
                            )
                            notificationSettings?.let { settings ->
                                UpdateTimeBeforeExpirationCard(
                                    settings.timeBeforeExpiration,
                                    onTimeSelected = { time ->
                                        viewModel.updateTimeBeforeExpiration(time)
                                    },
                                )
                            }
                            notificationSettings?.let { settings ->
                                NotificationSwitchList(settings, viewModel)
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
                if (showUpperTransition) {
                    UpperTransition()
                }
                if (showLowerTransition) {
                    LowerTransition(modifier = Modifier.align(Alignment.BottomCenter))
                }
            }
        }
    }
}
