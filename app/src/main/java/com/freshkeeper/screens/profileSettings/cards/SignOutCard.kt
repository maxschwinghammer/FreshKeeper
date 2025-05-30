package com.freshkeeper.screens.profileSettings.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun SignOutCard(onSignOutClick: () -> Unit) {
    var showExitAppDialog by remember { mutableStateOf(false) }

    AccountCenterCard(
        stringResource(R.string.sign_out),
        icon = Icons.AutoMirrored.Filled.ExitToApp,
        modifier = Modifier.card().border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        showExitAppDialog = true
    }

    if (showExitAppDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.sign_out_title), color = TextColor) },
            text = { Text(stringResource(R.string.sign_out_description), color = TextColor) },
            dismissButton = {
                Button(
                    onClick = { showExitAppDialog = false },
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
                        onSignOutClick()
                        showExitAppDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = RedColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(text = stringResource(R.string.sign_out))
                }
            },
            onDismissRequest = { showExitAppDialog = false },
        )
    }
}
