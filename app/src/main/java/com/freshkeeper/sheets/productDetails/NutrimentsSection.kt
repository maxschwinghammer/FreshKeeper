package com.freshkeeper.sheets.productDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.R
import com.freshkeeper.model.Nutriments
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun NutrimentsSection(
    nutriments: Nutriments,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(GreyColor)
                .border(
                    1.dp,
                    ComponentStrokeColor,
                    RoundedCornerShape(10.dp),
                ).padding(10.dp),
    ) {
        Text(
            stringResource(R.string.nutrition_heading),
            color = TextColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        nutriments.energyKcal?.let {
            Text(
                stringResource(R.string.energy, it.toString()),
                fontSize = 14.sp,
                color = TextColor,
            )
        }
        nutriments.fat?.let {
            Text(
                stringResource(R.string.fat, it.toString()),
                fontSize = 14.sp,
                color = TextColor,
            )
        }
        nutriments.carbohydrates?.let {
            Text(
                stringResource(R.string.carbohydrates, it.toString()),
                fontSize = 14.sp,
                color = TextColor,
            )
        }
        nutriments.sugars?.let {
            Text(
                stringResource(R.string.sugars, it.toString()),
                fontSize = 14.sp,
                color = TextColor,
            )
        }
        nutriments.fiber?.let {
            Text(
                stringResource(R.string.fiber, it.toString()),
                fontSize = 14.sp,
                color = TextColor,
            )
        }
        nutriments.proteins?.let {
            Text(
                stringResource(R.string.proteins, it.toString()),
                fontSize = 14.sp,
                color = TextColor,
            )
        }
        nutriments.salt?.let {
            Text(
                stringResource(R.string.salt, it.toString()),
                fontSize = 14.sp,
                color = TextColor,
            )
        }
    }
}
