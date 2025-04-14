package com.freshkeeper.screens.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.R
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.ExpiredColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun FoodList(
    title: String,
    image: Painter,
    items: List<Triple<String?, String, String>>,
    editProductSheetState: SheetState,
    onEditProduct: (String) -> Unit,
    isClickable: Boolean = true,
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier =
            Modifier
                .clip(RoundedCornerShape(15.dp))
                .background(ComponentBackgroundColor)
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(15.dp))
                .padding(16.dp),
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
                color =
                    if (title == stringResource(id = R.string.expired)) {
                        ExpiredColor
                    } else {
                        AccentTurquoiseColor
                    },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        items.forEach { (id, item, date) ->
            val safeId = id ?: ""

            val isMultiLine = item.length > 20
            val dynamicHeight = if (isMultiLine) 40.dp else Dp.Unspecified

            val rowModifier =
                Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
                    .then(
                        if (isClickable) {
                            Modifier.clickable {
                                coroutineScope.launch {
                                    editProductSheetState.show()
                                    onEditProduct(safeId)
                                }
                            }
                        } else {
                            Modifier
                        },
                    ).clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                    .clip(RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))

            Row(
                modifier = rowModifier,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .background(WhiteColor)
                            .padding(horizontal = 10.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.labelLarge,
                        color = ComponentBackgroundColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = true,
                    )
                }

                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .background(GreyColor)
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                            .heightIn(min = dynamicHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.labelLarge,
                        color = TextColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = true,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
