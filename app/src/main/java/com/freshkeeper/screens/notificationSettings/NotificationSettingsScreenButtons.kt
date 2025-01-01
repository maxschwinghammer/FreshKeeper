package com.freshkeeper.screens.notificationSettings

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.LightGreyColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun NotificationSwitchList() {
    val context = LocalContext.current
    val sharedPreferences =
        context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    val switches =
        listOf(
            "Food Added" to "food_added",
            "Household Changes" to "household_changes",
            "Food Expiring" to "food_expiring",
            "Tips" to "tips",
            "Statistics" to "statistics",
        )

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        switches.forEach { (title, key) ->
            NotificationSwitch(
                title = title,
                isChecked = sharedPreferences.getBoolean(key, false),
                onCheckedChange = { isChecked ->
                    sharedPreferences.edit().putBoolean(key, isChecked).apply()
                },
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun NotificationSwitch(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ComponentBackgroundColor),
        modifier =
            Modifier
                .fillMaxWidth()
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                color = Color.White,
                fontSize = 16.sp,
            )
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors =
                    SwitchDefaults.colors(
                        checkedBorderColor = ComponentStrokeColor,
                        checkedTrackColor = GreyColor,
                        checkedThumbColor = AccentTurquoiseColor,
                        uncheckedBorderColor = ComponentStrokeColor,
                        uncheckedTrackColor = GreyColor,
                        uncheckedThumbColor = LightGreyColor,
                    ),
                modifier = Modifier.scale(0.9f),
            )
        }
    }
}
