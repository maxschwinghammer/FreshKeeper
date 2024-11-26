package com.freshkeeper.screens.household

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.LightGreyColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun StatisticsSection(
    navController: NavController,
    mostWastedItems: List<Pair<String, String>>,
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.statistics),
                    color = AccentTurquoiseColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { navController.navigate("statistics") },
                ) {
                    Text(
                        text = stringResource(R.string.see_more),
                        color = LightGreyColor,
                        fontSize = 14.sp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.right_arrow),
                        contentDescription = "Arrow",
                        tint = LightGreyColor,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text =
                    stringResource(R.string.total_food_waste) + ": 15 " +
                        stringResource(R.string.items),
                color = TextColor,
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text =
                    stringResource(R.string.average_food_waste) + ": 0.5 " +
                        stringResource(R.string.items_per_day),
                color = TextColor,
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text =
                    stringResource(R.string.days_without_waste) + ": 25 " +
                        stringResource(R.string.days),
                color = TextColor,
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.most_wasted_food_items) + ":",
                color = TextColor,
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))

            mostWastedItems.forEach { (item, count) ->
                Row(
                    modifier =
                        Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                                .weight(1f)
                                .background(WhiteColor)
                                .padding(horizontal = 10.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.labelLarge,
                            color = ComponentBackgroundColor,
                            maxLines = 1,
                        )
                    }

                    Box(
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))
                                .weight(1f)
                                .background(GreyColor)
                                .padding(horizontal = 10.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = count,
                            style = MaterialTheme.typography.labelLarge,
                            color = TextColor,
                            maxLines = 1,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.waste_reduction) + ": 20%",
                color = TextColor,
                fontSize = 14.sp,
            )
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}
