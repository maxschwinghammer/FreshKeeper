package com.freshkeeper.screens.household

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.freshkeeper.R
import com.freshkeeper.screens.household.viewmodel.HouseholdViewModel
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.TextColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun MembersSection(
    navController: NavController,
    coroutineScope: CoroutineScope,
    inviteSheetState: SheetState,
    viewModel: HouseholdViewModel,
) {
    val members by viewModel.members.observeAsState(emptyList())

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(15.dp)),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = ComponentBackgroundColor),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = stringResource(id = R.string.members),
                color = AccentTurquoiseColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                members?.forEach { member ->
                    Box(
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    navController.navigate("profile")
                                }.width(75.dp)
                                .height(75.dp)
                                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                                .background(ComponentBackgroundColor)
                                .padding(10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Image(
                                painter = painterResource(id = member.imageId),
                                contentDescription = null,
                                modifier = Modifier.size(35.dp),
                            )
                            Text(
                                text = member.name,
                                color = TextColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
                Box(
                    modifier =
                        Modifier
                            .width(75.dp)
                            .height(75.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                            .background(ComponentBackgroundColor)
                            .clickable { coroutineScope.launch { inviteSheetState.show() } }
                            .padding(10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.add),
                            contentDescription = null,
                            modifier = Modifier.size(35.dp),
                        )
                        Text(
                            text = stringResource(R.string.invite),
                            color = TextColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}
