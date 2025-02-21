package com.freshkeeper.screens.householdSettings.cards

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.model.Household
import com.freshkeeper.screens.profileSettings.cards.AccountCenterCard
import com.freshkeeper.screens.profileSettings.cards.card
import com.freshkeeper.ui.theme.ComponentStrokeColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun InviteCard(
    inviteSheetState: SheetState,
    household: Household,
) {
    val coroutineScope = rememberCoroutineScope()
    val title =
        if (household.type == "Pair") {
            stringResource(R.string.invite_partner)
        } else {
            stringResource(R.string.invite_people)
        }

    AccountCenterCard(
        title = title,
        icon = painterResource(R.drawable.invite),
        modifier =
            Modifier
                .card()
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
        onCardClick = {
            coroutineScope.launch {
                inviteSheetState.show()
            }
        },
    )
}
