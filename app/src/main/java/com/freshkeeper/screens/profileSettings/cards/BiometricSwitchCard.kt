package com.freshkeeper.screens.profileSettings.cards

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.border
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.freshkeeper.R
import com.freshkeeper.screens.profileSettings.viewmodel.ProfileSettingsViewModel
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.LightGreyColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun BiometricSwitchCard(viewModel: ProfileSettingsViewModel = hiltViewModel()) {
    val activity = LocalActivity.current as? FragmentActivity
    Card(
        colors = CardDefaults.cardColors(containerColor = ComponentBackgroundColor),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        val isBiometricEnabled = viewModel.isBiometricEnabled.collectAsState().value

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.biometric_auth_title),
                modifier = Modifier.weight(1f),
                color = Color.White,
                fontSize = 16.sp,
            )
            Switch(
                checked = isBiometricEnabled,
                onCheckedChange = { isChecked ->
                    viewModel.onBiometricSwitchChanged(isChecked, activity)
                },
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
