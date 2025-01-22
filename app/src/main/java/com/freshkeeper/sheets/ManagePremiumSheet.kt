package com.freshkeeper.sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.model.Membership
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePremiumSheet(
    managePremiumSheetState: SheetState,
    membership: Membership,
    onCancelPremium: () -> Unit,
    onChangePlan: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    FreshKeeperTheme {
        ModalBottomSheet(
            onDismissRequest = { coroutineScope.launch { managePremiumSheetState.hide() } },
            sheetState = managePremiumSheetState,
            containerColor = ComponentBackgroundColor,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
            ) {
                Text(
                    text = "Manage premium membership",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Button(onClick = onCancelPremium) {
                    Text("Cancel premium membership")
                }
                Button(onClick = onChangePlan) {
                    Text("Change plan (monthly/yearly)")
                }
            }
        }
    }
}
