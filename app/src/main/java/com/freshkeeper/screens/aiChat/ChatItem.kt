package com.freshkeeper.screens.aiChat

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.BackgroundColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatItem(chatMessage: ChatMessage) {
    val isModelMessage =
        chatMessage.participant == Participant.MODEL ||
            chatMessage.participant == Participant.ERROR

    val backgroundColor =
        when (chatMessage.participant) {
            Participant.MODEL -> AccentTurquoiseColor
            Participant.USER -> GreyColor
            Participant.ERROR -> RedColor
        }

    val bubbleShape =
        if (isModelMessage) {
            RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
        } else {
            RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
        }

    val horizontalAlignment =
        if (isModelMessage) {
            Alignment.Start
        } else {
            Alignment.End
        }

    Column(
        horizontalAlignment = horizontalAlignment,
        modifier =
            Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth(),
    ) {
        Row {
            BoxWithConstraints {
                val boxWithConstraintsScope = this
                Card(
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    shape = bubbleShape,
                    modifier = Modifier.widthIn(0.dp, boxWithConstraintsScope.maxWidth * 0.9f),
                ) {
                    MarkdownText(
                        markdown = chatMessage.text,
                        modifier = Modifier.padding(12.dp),
                        textColor = if (isModelMessage) BackgroundColor else TextColor,
                    )
                }
            }
        }
    }
}
