package com.freshkeeper.screens.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
import com.freshkeeper.screens.notifications.viewmodel.NotificationsViewModel
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun NotificationsScreen(navController: NavHostController) {
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
    val notifications by notificationsViewModel.notifications.collectAsState()

    val listState = rememberLazyListState()
    val showUpperTransition by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }
    val showLowerTransition by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.size < notifications.size
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
                    BottomNavigationBar(selectedIndex = 4, navController, notificationsViewModel)
                }
            },
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.notifications),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                        modifier = Modifier.padding(16.dp),
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        if (notifications.isEmpty()) {
                            Column(modifier = Modifier.padding(start = 15.dp, end = 15.dp)) {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .background(
                                                ComponentBackgroundColor,
                                                RoundedCornerShape(10.dp),
                                            ).border(
                                                1.dp,
                                                ComponentStrokeColor,
                                                RoundedCornerShape(10.dp),
                                            ).padding(16.dp),
                                ) {
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            Image(
                                                modifier = Modifier.size(25.dp),
                                                contentDescription = null,
                                                painter = painterResource(R.drawable.check),
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Text(
                                                text = stringResource(R.string.no_pending_notifications),
                                                color = TextColor,
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                state = listState,
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .padding(start = 15.dp, end = 15.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                items(notifications) { notification ->
                                    NotificationCard(
                                        notification = notification,
                                        navController,
                                        onRemove = {
                                            notificationsViewModel.removeNotification(notification.id)
                                        },
                                    )
                                }
                                item {
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
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
    }
}
