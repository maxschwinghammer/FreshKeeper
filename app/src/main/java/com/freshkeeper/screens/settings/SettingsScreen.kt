package com.freshkeeper.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun SettingsScreen(
    navController: NavHostController,
    notificationsViewModel: NotificationsViewModel,
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
                    text = stringResource(R.string.settings),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor,
                    modifier = Modifier.padding(16.dp),
                )
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(top = 55.dp, start = 15.dp, end = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item {
                        Button(
                            onClick = { navController.navigate("memberProfile") },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = ComponentBackgroundColor,
                                    contentColor = TextColor,
                                ),
                            shape = RoundedCornerShape(10.dp),
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                            ) {
                                Text(
                                    text = stringResource(R.string.profile_settings),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextColor,
                                    modifier =
                                        Modifier.padding(
                                            top = 10.dp,
                                            bottom = 10.dp,
                                            end = 10.dp,
                                        ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
