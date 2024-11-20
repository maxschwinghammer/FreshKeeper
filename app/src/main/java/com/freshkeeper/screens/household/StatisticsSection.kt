package com.freshkeeper.screens.household

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.freshkeeper.R
import com.freshkeeper.ui.theme.AccentGreenColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.LightGreyColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun StatisticsSection(navController: NavController) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
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
                    text = "Statistics",
                    color = AccentGreenColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { navController.navigate("statistics") },
                ) {
                    Text(
                        text = "See more",
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
                text = "Total Amount of Food Waste: 15 items",
                color = TextColor,
                fontSize = 14.sp,
            )
            Text(
                text = "Average Food Waste per Day: 0.5 items/day",
                color = TextColor,
                fontSize = 14.sp,
            )
            Text(
                text = "Days with No Waste: 25 days",
                color = TextColor,
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Most Wasted Food Items:",
                color = TextColor,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Column {
                    Text(text = "Milk", color = TextColor)
                    Text(text = "Apples", color = TextColor)
                    Text(text = "Yogurt", color = TextColor)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "4 items", color = TextColor)
                    Text(text = "2 items", color = TextColor)
                    Text(text = "1 item", color = TextColor)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Percentage Reduction in Waste: 20%",
                color = TextColor,
                fontSize = 14.sp,
            )
        }
    }
}
