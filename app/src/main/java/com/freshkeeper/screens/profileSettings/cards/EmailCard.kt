package com.freshkeeper.screens.profileSettings.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.freshkeeper.R
import com.freshkeeper.model.User
import com.freshkeeper.screens.profileSettings.viewmodel.ProfileSettingsViewModel
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun EmailCard(
    viewModel: ProfileSettingsViewModel,
    navController: NavController,
    user: User,
) {
    var showChangeEmailDialog by remember { mutableStateOf(false) }
    var newEmail by remember { mutableStateOf("") }
    val cardTitle =
        String.format(
            stringResource(R.string.profile_email),
            user.email,
        )

    val icon: ImageVector? =
        if (user.provider != "google") {
            Icons.Filled.Edit
        } else {
            null
        }

    AccountCenterCard(
        cardTitle,
        icon = icon,
        modifier =
            Modifier
                .card()
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        if (user.provider != "google") {
            showChangeEmailDialog = true
        }
    }

    if (showChangeEmailDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.change_email_title), color = TextColor) },
            text = {
                Column {
                    Text(stringResource(R.string.change_email_description), color = TextColor)
                    Spacer(Modifier.padding(8.dp))
                    TextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        placeholder = { Text(user.email) },
                        colors =
                            TextFieldDefaults.colors(
                                unfocusedLabelColor = AccentTurquoiseColor,
                                focusedLabelColor = AccentTurquoiseColor,
                                cursorColor = AccentTurquoiseColor,
                                focusedTextColor = TextColor,
                                unfocusedTextColor = TextColor,
                                focusedContainerColor = GreyColor,
                                unfocusedContainerColor = GreyColor,
                                focusedIndicatorColor = AccentTurquoiseColor,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                        enabled = user.provider != "google",
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { showChangeEmailDialog = false },
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
                        viewModel.onChangeEmailClick(newEmail)
                        navController.navigate("signIn")
                        showChangeEmailDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = WhiteColor,
                            contentColor = GreyColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(text = stringResource(R.string.change_email))
                }
            },
            onDismissRequest = { showChangeEmailDialog = false },
        )
    }
}
