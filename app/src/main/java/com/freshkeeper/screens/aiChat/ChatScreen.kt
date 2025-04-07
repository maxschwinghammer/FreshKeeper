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
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
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
import com.freshkeeper.screens.LowerTransition
import com.freshkeeper.screens.UpperTransition
import com.freshkeeper.screens.aiChat.viewmodel.ChatViewModel
import com.freshkeeper.screens.inventory.viewmodel.InventoryViewModel
import com.freshkeeper.screens.notifications.viewmodel.NotificationsViewModel
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.BackgroundColor
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.RedColor
import com.freshkeeper.ui.theme.TextColor
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatScreen(navController: NavHostController) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
    val inventoryViewModel: InventoryViewModel = hiltViewModel()
    val itemList by inventoryViewModel.itemList.observeAsState("")
    val chatViewModel: ChatViewModel = viewModel(factory = GenerativeViewModelFactory)
    val chatUiState by chatViewModel.uiState.collectAsState()

    val welcomeText = stringResource(R.string.welcome_text)
    val roleText = stringResource(R.string.ai_memory_block)
    val productsText = stringResource(R.string.current_food_items)
    val languageText = stringResource(R.string.answer_in)

    LaunchedEffect(Unit) {
        chatViewModel.initializeTexts(welcomeText, roleText, productsText, languageText)
    }

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
                        chatMessages = chatUiState.messages,
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
                    text = stringResource(R.string.ai_chat) + " (Beta)",
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
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AccentTurquoiseColor),
                            shape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp),
                            modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f),
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
                ChatBubbleItem(message)
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

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatBubbleItem(chatMessage: ChatMessage) {
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
                Card(
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    shape = bubbleShape,
                    modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f),
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
