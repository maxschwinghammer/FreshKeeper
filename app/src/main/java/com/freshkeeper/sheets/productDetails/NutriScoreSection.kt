package com.freshkeeper.sheets.productDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.R
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun NutriScoreSection(
    score: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(10.dp))
                .background(GreyColor)
                .border(
                    1.dp,
                    ComponentStrokeColor,
                    RoundedCornerShape(10.dp),
                ).padding(top = 10.dp, start = 10.dp, end = 10.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Text(
            stringResource(R.string.nutri_score_heading),
            color = TextColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))
        val imageId =
            when (score.uppercase()) {
                "A" -> R.drawable.nutri_score_a
                "B" -> R.drawable.nutri_score_b
                "C" -> R.drawable.nutri_score_c
                "D" -> R.drawable.nutri_score_d
                "E" -> R.drawable.nutri_score_e
                else -> null
            }
        imageId?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription =
                    stringResource(
                        R.string.nutri_score_desc,
                        score,
                    ),
                modifier = Modifier.width(90.dp),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
