package com.freshkeeper.screens.notificationSettings.cards

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.screens.profileSettings.cards.card
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun NotificationPermissionCard(
    context: Context,
    isNotificationEnabled: Boolean,
) {
    Card(
        modifier =
            Modifier.card().border(
                1.dp,
                ComponentStrokeColor,
                RoundedCornerShape(10.dp),
            ),
        onClick = {
            val intent =
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(
                        Settings.EXTRA_APP_PACKAGE,
                        context.packageName,
                    )
                }
            context.startActivity(intent)
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .background(ComponentBackgroundColor)
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (isNotificationEnabled) {
                    Text(text = stringResource(R.string.revoke_notification_permissions))
                } else {
                    Text(text = stringResource(R.string.grant_notification_permissions))
                }
            }
            Image(
                painter = painterResource(R.drawable.notification),
                contentDescription = "Icon",
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
