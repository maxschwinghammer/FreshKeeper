package com.freshkeeper.screens.householdSettings.cards

import android.content.ClipData.newPlainText
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.screens.profileSettings.cards.AccountCenterCard
import com.freshkeeper.screens.profileSettings.cards.card
import com.freshkeeper.ui.theme.ComponentStrokeColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun HouseholdIdCard(householdId: String) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    AccountCenterCard(
        title = stringResource(R.string.household_id) + ":\n" + householdId,
        icon = painterResource(R.drawable.copy),
        modifier =
            Modifier
                .card()
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
    ) {
        val clip = newPlainText("Household ID", householdId)
        clipboardManager.setPrimaryClip(clip)
    }
}
