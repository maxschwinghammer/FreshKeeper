package com.freshkeeper.screens.household

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.freshkeeper.R
import com.freshkeeper.model.Activity
import com.freshkeeper.screens.household.viewmodel.HouseholdViewModel
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@Composable
fun ActivitiesSection(isStory: Boolean = false) {
    val viewModel: HouseholdViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val activities by viewModel.activities.observeAsState(emptyList())

    val storyActivities =
        listOf(
            Activity(
                id = "1",
                userId = "1",
                type = "user_joined",
                text = stringResource(R.string.activity_tim_joined),
                timestamp = System.currentTimeMillis(),
            ),
            Activity(
                id = "2",
                userId = "2",
                type = "add_product",
                text = stringResource(R.string.activity_emma_added_product),
                timestamp = System.currentTimeMillis(),
            ),
            Activity(
                id = "3",
                userId = "3",
                type = "consumed",
                text = stringResource(R.string.activity_paul_consumed_milk),
                timestamp = System.currentTimeMillis(),
            ),
        )

    val drawableMap =
        mapOf(
            "user_joined" to R.drawable.user_joined,
            "add_product" to R.drawable.plus,
            "edit" to R.drawable.edit,
            "consumed" to R.drawable.remove,
            "thrown_away" to R.drawable.remove,
            "update" to R.drawable.update,
        )

    fun getDrawableId(imageId: String): Int = drawableMap[imageId] ?: R.drawable.plus

    if (isStory) {
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
                    text = stringResource(id = R.string.activities),
                    color = AccentTurquoiseColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                storyActivities.forEach { activity ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                                .background(ComponentBackgroundColor, RoundedCornerShape(10.dp))
                                .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(id = getDrawableId(activity.type)),
                            contentDescription = null,
                            tint = WhiteColor,
                            modifier = Modifier.size(15.dp),
                        )
                        Text(
                            text = activity.text,
                            color = TextColor,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 10.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (activities?.isNotEmpty() == true) {
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
                    text = stringResource(id = R.string.activities),
                    color = AccentTurquoiseColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                activities?.forEach { activity ->
                    var offsetX by remember { mutableFloatStateOf(0f) }
                    val animatedOffsetX by animateDpAsState(targetValue = offsetX.dp, label = "")

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Max)
                                .background(Color.Transparent),
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                                    .background(RedColor)
                                    .padding(end = 16.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.delete),
                                contentDescription = "Delete Icon",
                                tint = WhiteColor,
                                modifier = Modifier.size(15.dp),
                            )
                        }

                        Row(
                            modifier =
                                Modifier
                                    .offset { IntOffset(animatedOffsetX.roundToPx(), 0) }
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                                    .background(ComponentBackgroundColor, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                                    .pointerInput(Unit) {
                                        detectHorizontalDragGestures(
                                            onDragEnd = {
                                                if (offsetX < -100) {
                                                    coroutineScope.launch {
                                                        viewModel.removeActivity(activity)
                                                    }
                                                    offsetX = 0f
                                                } else {
                                                    offsetX = 0f
                                                }
                                            },
                                            onHorizontalDrag = { _, dragAmount ->
                                                offsetX =
                                                    (offsetX + dragAmount)
                                                        .coerceAtMost(0f)
                                            },
                                        )
                                    },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(id = getDrawableId(activity.type)),
                                contentDescription = null,
                                tint = WhiteColor,
                                modifier = Modifier.size(15.dp),
                            )
                            Text(
                                text = activity.text,
                                color = TextColor,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth(),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
