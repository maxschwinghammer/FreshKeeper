package com.freshkeeper.screens.help

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor

@OptIn(ExperimentalLayoutApi::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun FAQAccordion(
    question: String,
    answer: String,
    isExpanded: Boolean,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .background(GreyColor, RoundedCornerShape(10.dp))
                    .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                    .clickable { onClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = question,
                color = TextColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f).padding(10.dp),
            )
            Icon(
                imageVector =
                    if (isExpanded) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                contentDescription = null,
                modifier = Modifier.padding(10.dp),
                tint = TextColor,
            )
        }
        if (isExpanded) {
            val context = LocalContext.current
            val link = "https://freshkeeper.de/privacy-policy"

            if (answer.contains(link)) {
                val parts = answer.split(link)

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(GreyColor, RoundedCornerShape(10.dp))
                            .border(1.dp, GreyColor, RoundedCornerShape(10.dp)),
                ) {
                    FlowRow(
                        modifier = Modifier.padding(10.dp),
                    ) {
                        Text(
                            text = parts.getOrNull(0) ?: "",
                            color = TextColor,
                            fontSize = 12.sp,
                        )
                        Text(
                            text = link,
                            color = AccentTurquoiseColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier =
                                Modifier.clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, link.toUri())
                                    context.startActivity(intent)
                                },
                        )
                        Text(
                            text = parts.getOrNull(1) ?: "",
                            color = TextColor,
                            fontSize = 12.sp,
                        )
                    }
                }
            } else {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(GreyColor, RoundedCornerShape(10.dp))
                            .border(1.dp, GreyColor, RoundedCornerShape(10.dp)),
                ) {
                    Text(
                        text = answer,
                        color = TextColor,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
