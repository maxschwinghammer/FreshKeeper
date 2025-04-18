package com.freshkeeper.screens.aiChat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.freshkeeper.screens.LowerTransition
import com.freshkeeper.screens.UpperTransition
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.BackgroundColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatList(
    chatMessages: List<ChatMessage>,
    listState: LazyListState,
) {
    val showLowerTransition by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }
    val showUpperTransition by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.size < chatMessages.size ||
                listState.firstVisibleItemIndex > 1
        }
    }

    Box {
        LazyColumn(
            reverseLayout = true,
            state = listState,
        ) {
            if (chatMessages.lastOrNull()?.participant == Participant.USER &&
                chatMessages.lastOrNull()?.isPending == true
            ) {
                item {
                    BoxWithConstraints {
                        val boxWithConstraintsScope = this
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AccentTurquoiseColor),
                            shape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp),
                            modifier = Modifier.widthIn(0.dp, boxWithConstraintsScope.maxWidth * 0.9f),
                        ) {
                            Box(
                                modifier = Modifier.padding(12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp).padding(4.dp),
                                    strokeWidth = 2.dp,
                                    color = BackgroundColor,
                                )
                            }
                        }
                    }
                }
            }
            items(chatMessages.reversed()) { message ->
                ChatItem(message)
            }
        }
        if (showUpperTransition) {
            UpperTransition()
        }
        if (showLowerTransition) {
            LowerTransition(
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}
