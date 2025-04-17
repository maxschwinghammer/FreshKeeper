package com.freshkeeper.screens.aiChat.viewmodel

import android.os.Build
import androidx.lifecycle.viewModelScope
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.screens.aiChat.ChatMessage
import com.freshkeeper.screens.aiChat.ChatUiState
import com.freshkeeper.screens.aiChat.Participant
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    generativeModel: GenerativeModel,
) : AppViewModel() {
    private var isFirstMessage = true
    private var products = ""
    private var language = ""

    private val _uiState: MutableStateFlow<ChatUiState> = MutableStateFlow(ChatUiState(emptyList()))
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val chat = generativeModel.startChat()

    fun initializeTexts(
        welcomeText: String,
        roleText: String,
        productsText: String,
        languageText: String,
    ) {
        products = productsText
        language = languageText

        _uiState.value.addMessage(
            ChatMessage(
                text = roleText,
                participant = Participant.USER,
                isPending = false,
            ),
        )

        _uiState.value.addMessage(
            ChatMessage(
                text = welcomeText,
                participant = Participant.MODEL,
                isPending = false,
            ),
        )
    }

    fun sendMessage(
        userMessage: String,
        itemList: String,
    ) {
        _uiState.value.addMessage(
            ChatMessage(
                text = userMessage,
                participant = Participant.MODEL,
                isPending = true,
            ),
        )

        viewModelScope.launch {
            try {
                val response =
                    if (isFirstMessage) {
                        isFirstMessage = false
                        chat.sendMessage(
                            "$userMessage $products $itemList $language",
                        )
                    } else {
                        chat.sendMessage(userMessage)
                    }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    _uiState.value.replaceLastPendingMessage()
                }

                response.text?.let { modelResponse ->
                    _uiState.value.addMessage(
                        ChatMessage(
                            text = modelResponse,
                            participant = Participant.MODEL,
                            isPending = false,
                        ),
                    )
                }
            } catch (e: Exception) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    _uiState.value.replaceLastPendingMessage()
                }
                e.localizedMessage
                    ?.let {
                        ChatMessage(
                            text = it,
                            participant = Participant.ERROR,
                        )
                    }?.let {
                        _uiState.value.addMessage(
                            it,
                        )
                    }
            }
        }
    }
}
