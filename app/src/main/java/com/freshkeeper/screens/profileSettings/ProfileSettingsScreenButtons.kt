package com.freshkeeper.screens.profileSettings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.freshkeeper.R
import com.freshkeeper.model.User
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun AccountCenterCard(
    title: String,
    icon: Any,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = onCardClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .background(ComponentBackgroundColor)
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) { Text(title, color = TextColor) }
            when (icon) {
                is ImageVector ->
                    Icon(
                        imageVector = icon,
                        contentDescription = "Icon",
                        modifier = Modifier.size(24.dp),
                    )
                is Painter ->
                    Icon(
                        painter = icon,
                        contentDescription = "Icon",
                        modifier = Modifier.size(24.dp),
                    )
                else -> throw IllegalArgumentException("Unsupported icon type")
            }
        }
    }
}

fun Modifier.card(): Modifier = this.padding(16.dp, 0.dp, 16.dp, 0.dp)

@Suppress("ktlint:standard:function-naming")
@Composable
fun DisplayNameCard(
    displayName: String,
    onUpdateDisplayNameClick: (String) -> Unit,
) {
    var showDisplayNameDialog by remember { mutableStateOf(false) }
    var newDisplayName by remember { mutableStateOf(displayName) }
    val cardTitle = displayName.ifBlank { stringResource(R.string.profile_name) }

    AccountCenterCard(
        "Name: $cardTitle",
        Icons.Filled.Edit,
        Modifier
            .card()
            .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        newDisplayName = displayName
        showDisplayNameDialog = true
    }

    if (showDisplayNameDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.profile_name)) },
            text = {
                Column {
                    TextField(
                        value = newDisplayName,
                        colors =
                            TextFieldDefaults.colors(
                                focusedTextColor = TextColor,
                                unfocusedTextColor = TextColor,
                                focusedContainerColor = GreyColor,
                                unfocusedContainerColor = GreyColor,
                                focusedIndicatorColor = AccentTurquoiseColor,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                        onValueChange = { newDisplayName = it },
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDisplayNameDialog = false },
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
                        onUpdateDisplayNameClick(newDisplayName)
                        showDisplayNameDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = AccentTurquoiseColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.update))
                }
            },
            onDismissRequest = { showDisplayNameDialog = false },
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun EmailCard(
    viewModel: ProfileSettingsScreenViewModel,
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

    AccountCenterCard(
        cardTitle,
        Icons.Filled.Edit,
        Modifier
            .card()
            .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        showChangeEmailDialog = true
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
                                focusedTextColor = TextColor,
                                unfocusedTextColor = TextColor,
                                focusedContainerColor = GreyColor,
                                unfocusedContainerColor = GreyColor,
                                focusedIndicatorColor = AccentTurquoiseColor,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
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
                    Text(text = stringResource(R.string.cancel), color = TextColor)
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
                            containerColor = AccentTurquoiseColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.change_email), color = TextColor)
                }
            },
            onDismissRequest = { showChangeEmailDialog = false },
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ResetPasswordCard(
    viewModel: ProfileSettingsScreenViewModel,
    navController: NavController,
) {
    var showResetPasswordDialog by remember { mutableStateOf(false) }

    AccountCenterCard(
        stringResource(R.string.reset_password),
        icon = painterResource(R.drawable.reset_password),
        Modifier.card().border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
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
                    Text(text = stringResource(R.string.cancel), color = TextColor)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onResetPasswordClick {
                            navController.navigate("signIn") {
                                popUpTo(0)
                            }
                        }
                        showResetPasswordDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = RedColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.reset), color = TextColor)
                }
            },
            onDismissRequest = { showResetPasswordDialog = false },
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun SignOutCard(onSignOutClick: () -> Unit) {
    var showExitAppDialog by remember { mutableStateOf(false) }

    AccountCenterCard(
        stringResource(R.string.sign_out),
        Icons.AutoMirrored.Filled.ExitToApp,
        Modifier.card().border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
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
                    Text(text = stringResource(R.string.cancel), color = TextColor)
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
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.sign_out), color = TextColor)
                }
            },
            onDismissRequest = { showExitAppDialog = false },
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun RemoveAccountCard(onRemoveAccountClick: () -> Unit) {
    var showRemoveAccDialog by remember { mutableStateOf(false) }

    AccountCenterCard(
        stringResource(R.string.delete_account),
        Icons.Filled.Delete,
        Modifier.card().border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        showRemoveAccDialog = true
    }

    if (showRemoveAccDialog) {
        AlertDialog(
            containerColor = ComponentBackgroundColor,
            title = { Text(stringResource(R.string.delete_account_title)) },
            text = { Text(stringResource(R.string.delete_account_description), color = TextColor) },
            dismissButton = {
                Button(
                    onClick = { showRemoveAccDialog = false },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = GreyColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.cancel), color = TextColor)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRemoveAccountClick()
                        showRemoveAccDialog = false
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = RedColor,
                            contentColor = TextColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(text = stringResource(R.string.delete_account), color = TextColor)
                }
            },
            onDismissRequest = { showRemoveAccDialog = false },
        )
    }
}
