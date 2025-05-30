package com.freshkeeper.screens.profileSettings.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.freshkeeper.R
import com.freshkeeper.screens.profileSettings.viewmodel.ProfileSettingsViewModel
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun ResetPasswordCard(
    viewModel: ProfileSettingsViewModel,
    navController: NavController,
) {
    var showResetPasswordDialog by remember { mutableStateOf(false) }

    AccountCenterCard(
        stringResource(R.string.reset_password),
        icon = painterResource(R.drawable.reset_password),
        modifier = Modifier.card().border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        showResetPasswordDialog = true
    }

    if (showResetPasswordDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.reset_password_title), color = TextColor) },
            text = { Text(stringResource(R.string.reset_password_description), color = TextColor) },
            dismissButton = {
                Button(
                    onClick = { showResetPasswordDialog = false },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = GreyColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onResetPasswordClick {
                            navController.navigate("signIn") { popUpTo(0) { inclusive = true } }
                        }
                        showResetPasswordDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = RedColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(text = stringResource(R.string.reset))
                }
            },
            onDismissRequest = { showResetPasswordDialog = false },
        )
    }
}
