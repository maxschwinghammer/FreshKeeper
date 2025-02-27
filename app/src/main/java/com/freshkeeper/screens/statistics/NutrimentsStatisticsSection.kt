package com.freshkeeper.screens.statistics

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import com.freshkeeper.model.Nutriments
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun NutrimentsStatisticsSection(
    averageNutriments: Nutriments,
    averageNutriScore: String,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(bottom = 20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.nutriments_statistics),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AccentTurquoiseColor,
                modifier = Modifier.weight(1f),
            )
            Image(
                painter = painterResource(R.drawable.share),
                contentDescription = "Share",
                modifier = Modifier.size(20.dp),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                averageNutriments.energyKcal?.let {
                    Text(
                        text = stringResource(R.string.energy, it.toString()),
                        fontSize = 14.sp,
                        color = TextColor,
                    )
                }
                averageNutriments.fat?.let {
                    Text(
                        text =
                            stringResource(
                                R.string.fat,
                                it.toString(),
                            ),
                        fontSize = 14.sp,
                        color = TextColor,
                    )
                }
                averageNutriments.carbohydrates?.let {
                    Text(
                        text =
                            stringResource(
                                R.string.carbohydrates,
                                it.toString(),
                            ),
                        fontSize = 14.sp,
                        color = TextColor,
                    )
                }
                averageNutriments.sugars?.let {
                    Text(
                        text = stringResource(R.string.sugars, it.toString()),
                        fontSize = 14.sp,
                        color = TextColor,
                    )
                }
                averageNutriments.fiber?.let {
                    Text(
                        text = stringResource(R.string.fiber, it.toString()),
                        fontSize = 14.sp,
                        color = TextColor,
                    )
                }
                averageNutriments.proteins?.let {
                    Text(
                        text = stringResource(R.string.proteins, it.toString()),
                        fontSize = 14.sp,
                        color = TextColor,
                    )
                }
                averageNutriments.salt?.let {
                    Text(
                        text = stringResource(R.string.salt, it.toString()),
                        fontSize = 14.sp,
                        color = TextColor,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(0.7f),
            ) {
                Text(
                    text = stringResource(R.string.nutri_score) + ":",
                    fontSize = 14.sp,
                    color = TextColor,
                )
                Spacer(modifier = Modifier.height(10.dp))
                val imageId =
                    when (averageNutriScore.uppercase()) {
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
                                averageNutriScore,
                            ),
                        modifier = Modifier.width(90.dp),
                    )
                }
            }
        }
    }
}
