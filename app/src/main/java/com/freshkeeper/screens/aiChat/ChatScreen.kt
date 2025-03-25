package com.freshkeeper.screens.aiChat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.aiChat.viewmodel.ChatViewModel
import com.freshkeeper.screens.inventory.viewmodel.InventoryViewModel
import com.freshkeeper.screens.notifications.viewmodel.NotificationsViewModel
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.LightGreyColor
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@Composable
internal fun ChatScreen(navController: NavHostController) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
    val inventoryViewModel: InventoryViewModel = hiltViewModel()
    val itemList by inventoryViewModel.itemList.observeAsState("")
    val chatViewModel: ChatViewModel = viewModel(factory = GenerativeViewModelFactory)
    val chatUiState by chatViewModel.uiState.collectAsState()

    FreshKeeperTheme {
        Scaffold(
            bottomBar = {
                Column {
                    MessageInput(
                        onSendMessage = { inputText ->
                            chatViewModel.sendMessage(inputText, itemList)
                        },
                        resetScroll = {
                            coroutineScope.launch {
                                if (listState.layoutInfo.visibleItemsInfo.isEmpty()) {
                                    listState.scrollToItem(0)
                                }
                            }
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier =
                            Modifier
                                .background(BottomNavBackgroundColor)
                                .padding(horizontal = 10.dp),
                    ) {
                        BottomNavigationBar(
                            selectedIndex = 0,
                            navController,
                            notificationsViewModel,
                        )
                    }
                }
            },
        ) { paddingValues ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
            ) {
                Text(
                    text = stringResource(R.string.ai_chat),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor,
                    modifier =
                        Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                )
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(start = 15.dp, end = 15.dp),
                ) {
                    ChatList(chatUiState.getVisibleMessages(), listState)
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatList(
    chatMessages: List<ChatMessage>,
    listState: LazyListState,
) {
    LazyColumn(
        reverseLayout = true,
        state = listState,
    ) {
        items(chatMessages.reversed()) { message ->
            ChatBubbleItem(message)
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatBubbleItem(chatMessage: ChatMessage) {
    val isModelMessage =
        chatMessage.participant == Participant.MODEL ||
            chatMessage.participant == Participant.ERROR

    val backgroundColor =
        when (chatMessage.participant) {
            Participant.MODEL -> GreyColor
            Participant.USER -> LightGreyColor
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
                Card(
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    shape = bubbleShape,
                    modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f),
                ) {
                    MarkdownText(
                        markdown = chatMessage.text,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    resetScroll: () -> Unit = {},
) {
    var userMessage by rememberSaveable { mutableStateOf("") }
    val suggestions =
        listOf(
            stringResource(R.string.suggestion_create_recipe),
            stringResource(R.string.suggestion_storage_tips),
            stringResource(R.string.suggestion_get_tips),
        )

    Spacer(modifier = Modifier.height(8.dp))
    LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
        items(suggestions) { suggestion ->
            ElevatedCard(
                onClick = {
                    onSendMessage(suggestion)
                    resetScroll()
                },
                colors = CardDefaults.cardColors(containerColor = GreyColor),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(end = 8.dp),
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
            modifier =
                Modifier
                    .padding(vertical = 12.dp, horizontal = 12.dp)
                    .fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = userMessage,
                label = null,
                onValueChange = { userMessage = it },
                keyboardOptions =
                    KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                    ),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = ComponentStrokeColor,
                        focusedBorderColor = AccentTurquoiseColor,
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
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically)
                        .fillMaxWidth()
                        .weight(0.15f),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.action_send),
                    modifier = Modifier,
                )
            }
        }
    }
}
