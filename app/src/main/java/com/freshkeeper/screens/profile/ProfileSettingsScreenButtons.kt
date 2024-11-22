package com.freshkeeper.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.ui.theme.AccentGreenColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor

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
        cardTitle,
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
                                focusedIndicatorColor = AccentGreenColor,
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
                            containerColor = GreyColor,
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
fun AccountCenterCard(
    title: String,
    icon: ImageVector,
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
            Icon(icon, contentDescription = "Icon")
        }
    }
}

fun Modifier.card(): Modifier = this.padding(16.dp, 0.dp, 16.dp, 8.dp)

@Suppress("ktlint:standard:function-naming")
@Composable
fun ExitAppCard(onSignOutClick: () -> Unit) {
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
