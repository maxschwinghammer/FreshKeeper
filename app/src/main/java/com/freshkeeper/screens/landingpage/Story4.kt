package com.freshkeeper.screens.landingpage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.model.Notification
import com.freshkeeper.model.NotificationText
import com.freshkeeper.screens.notifications.NotificationCard

@OptIn(ExperimentalComposeUiApi::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun Story4(navController: NavHostController) {
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
        )

    StoryTemplate(
        headline = R.string.push_notifications,
        content = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp),
                contentAlignment = Alignment.Center,
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    items(notifications) { notification ->
                        NotificationCard(
                            notification = notification,
                            navController,
                            onRemove = {},
                            isClickable = false,
                        )
                    }
                }
            }
        },
    )
}
