package com.freshkeeper.screens.aiChat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
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
