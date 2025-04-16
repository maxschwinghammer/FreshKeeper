package com.freshkeeper.screens.landingpage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.freshkeeper.R
import com.freshkeeper.screens.household.ActivitiesSection
import com.freshkeeper.screens.household.MembersSection

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun Story3() {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val inviteSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    StoryTemplate(
        headline = R.string.manage_your_household,
        content = {
            Column(
                modifier = Modifier.padding(horizontal = 30.dp),
            ) {
                MembersSection(
                    navController,
                    coroutineScope,
                    inviteSheetState,
                    onCreateHouseholdClick = { name, type -> },
                    onJoinHouseholdClick = {},
                    onAddProducts = { },
                    onDeleteProducts = { },
                    isStory = true,
                )
                ActivitiesSection(isStory = true)
            }
        },
    )
}
