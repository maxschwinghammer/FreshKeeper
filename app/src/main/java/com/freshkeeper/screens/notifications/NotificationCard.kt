package com.freshkeeper.screens.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.freshkeeper.model.Notification
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun NotificationCard(
    notification: Notification,
    navController: NavHostController,
    onRemove: () -> Unit,
    isClickable: Boolean = true,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(ComponentBackgroundColor, RoundedCornerShape(10.dp))
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                .padding(16.dp),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Image(
                    modifier = Modifier.size(25.dp),
                    contentDescription = null,
                    painter = painterResource(id = notification.imageResId),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text =
                        stringResource(
                            id = notification.title.resId,
                            *notification.title.params.toTypedArray(),
                        ),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "✕",
                    color = TextColor,
                    modifier =
                        if (!isClickable) {
                            Modifier.clickable { onRemove() }
                        } else {
                            Modifier
                        },
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text =
                    stringResource(
                        id = notification.description.resId,
                        *notification.description.params.toTypedArray(),
                    ),
                fontSize = 14.sp,
                color = TextColor,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                enabled = isClickable,
                onClick = { navController.navigate(notification.destinationScreen) },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = TextColor,
                        disabledContainerColor = TextColor,
                    ),
            ) {
                Text(
                    text =
                        stringResource(
                            id = notification.buttonText.resId,
                            *notification.buttonText.params.toTypedArray(),
                        ),
                    color = ComponentBackgroundColor,
                )
            }
        }
    }
}
