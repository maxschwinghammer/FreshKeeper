package com.freshkeeper.screens.aiChat

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.R
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    resetScroll: () -> Unit = {},
    chatMessages: List<ChatMessage>,
) {
    var userMessage by rememberSaveable { mutableStateOf("") }
    val isPending = chatMessages.lastOrNull()?.isPending == true

    val suggestions =
        listOf(
            stringResource(R.string.suggestion_create_recipe),
            stringResource(R.string.suggestion_storage_tips),
            stringResource(R.string.suggestion_get_tips),
            stringResource(R.string.suggestion_meal_prep),
        )

    Spacer(modifier = Modifier.height(8.dp))
    LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
        items(suggestions) { suggestion ->
            ElevatedCard(
                onClick = {
                    if (!isPending) {
                        onSendMessage(suggestion)
                        resetScroll()
                    }
                },
                colors = CardDefaults.cardColors(containerColor = GreyColor),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(end = 8.dp).alpha(if (isPending) 0.5f else 1f),
            ) {
                Text(
                    text = suggestion,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontSize = 14.sp,
                    color = TextColor,
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))

    ElevatedCard(
        modifier =
            Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GreyColor),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = userMessage,
                label = null,
                onValueChange = { userMessage = it },
                placeholder = { Text(stringResource(R.string.enter_message)) },
                keyboardOptions =
                    KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                    ),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        cursorColor = AccentTurquoiseColor,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedLabelColor = TextColor,
                        focusedLabelColor = AccentTurquoiseColor,
                    ),
                textStyle = TextStyle(fontSize = 16.sp),
                modifier =
                    Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxWidth()
                        .weight(1f)
                        .imePadding(),
            )
            IconButton(
                onClick = {
                    if (userMessage.isNotBlank()) {
                        onSendMessage(userMessage)
                        userMessage = ""
                        resetScroll()
                    }
                },
                modifier =
                    Modifier
                        .padding(horizontal = 8.dp)
                        .align(Alignment.CenterVertically)
                        .fillMaxWidth()
                        .weight(0.15f),
                enabled = !isPending,
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.action_send),
                    tint = AccentTurquoiseColor,
                )
            }
        }
    }
}
