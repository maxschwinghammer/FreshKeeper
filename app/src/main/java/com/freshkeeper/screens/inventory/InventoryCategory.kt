package com.freshkeeper.screens.inventory

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.TextColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun InventoryCategory(
    title: String,
    image: Painter,
    items: List<String>,
    editProductSheetState: SheetState,
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(ComponentBackgroundColor)
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(15.dp))
                .padding(10.dp),
    ) {
        Row {
            Image(
                modifier = Modifier.size(25.dp),
                contentDescription = null,
                painter = image,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AccentTurquoiseColor,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items.forEach { item ->
                Box(
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                coroutineScope.launch { editProductSheetState.show() }
                            }.border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.labelLarge,
                        color = TextColor,
                    )
                }
            }
        }
    }
}
