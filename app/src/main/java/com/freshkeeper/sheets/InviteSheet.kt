package com.freshkeeper.sheets

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.R
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteSheet(
    qrCodeSheetState: SheetState,
    inviteSheetState: SheetState,
    addUserByIdSheetState: SheetState,
    householdId: String,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(inviteSheetState.isVisible) {
        if (inviteSheetState.isVisible) {
            coroutineScope.launch {
                qrCodeSheetState.hide()
            }
        }
    }

    FreshKeeperTheme {
        ModalBottomSheet(
            onDismissRequest = { coroutineScope.launch { inviteSheetState.hide() } },
            sheetState = inviteSheetState,
            containerColor = ComponentBackgroundColor,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.invite_text),
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(16.dp))

          /*      AddEntryButton(
                    text = "QR-Code",
                    iconId = R.drawable.qr_code,
                    onClick = {
                        coroutineScope.launch {
                            inviteSheetState.hide()
                            qrCodeSheetState.show()
                        }
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))*/
                AddEntryButton(
                    text = stringResource(R.string.share),
                    iconId = R.drawable.share,
                    onClick = {
                        val message = context.getString(R.string.invite_message) + " " + householdId
                        val shareIntent =
                            Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, message)
                            }
                        context.startActivity(
                            Intent.createChooser(shareIntent, context.getString(R.string.share)),
                        )
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                AddEntryButton(
                    text = stringResource(R.string.add_user_by_id),
                    iconId = R.drawable.invite,
                    onClick = {
                        coroutineScope.launch {
                            inviteSheetState.hide()
                            addUserByIdSheetState.show()
                        }
                    },
                )
            }
        }
    }
}
