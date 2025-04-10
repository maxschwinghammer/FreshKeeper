package com.freshkeeper.screens.landingpage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.model.Notification
import com.freshkeeper.model.NotificationText
import com.freshkeeper.screens.notifications.NotificationCard

@Suppress("ktlint:standard:function-naming")
@Composable
fun Story3(navController: NavHostController) {
    val listState = rememberLazyListState()
    val notifications =
        listOf<Notification>(
            Notification(
                title = NotificationText(resId = R.string.joghurt_expires_tomorrow_title),
                id = "1",
                type = "food_expiring",
                destinationScreen = "home",
                description = NotificationText(resId = R.string.joghurt_expires_tomorrow_description),
                buttonText = NotificationText(resId = R.string.see_expiring_food),
                imageResId = R.drawable.warning,
            ),
            Notification(
                title = NotificationText(resId = R.string.your_progress_this_week_title),
                id = "2",
                type = "statistics",
                destinationScreen = "statistics",
                description = NotificationText(resId = R.string.your_progress_this_week_description),
                buttonText = NotificationText(resId = R.string.see_statistics),
                imageResId = R.drawable.statistics,
            ),
            Notification(
                title = NotificationText(resId = R.string.tip_of_the_day_title),
                id = "3",
                type = "tips",
                destinationScreen = "tips",
                description = NotificationText(resId = R.string.tip_of_the_day_description),
                buttonText = NotificationText(resId = R.string.view_more_tips),
                imageResId = R.drawable.tip,
            ),
        )

    StoryTemplate(
        headline = "Erhalte Push-Benachrichtigungen",
        content = {
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
                        onRemove = {},
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        },
    )
}
