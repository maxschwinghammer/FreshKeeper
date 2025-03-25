package com.freshkeeper.screens.aiChat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.toMutableStateList

class ChatUiState(
    messages: List<ChatMessage> = emptyList(),
) {
    private val _messages: MutableList<ChatMessage> = messages.toMutableStateList()
    val messages: List<ChatMessage> = _messages

    fun addMessage(msg: ChatMessage) {
        _messages.add(msg)
    }

    fun getVisibleMessages(): List<ChatMessage> = messages.filterIndexed { index, _ -> index > 0 }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun replaceLastPendingMessage() {
        val lastMessage = _messages.lastOrNull()
        lastMessage?.let {
            val newMessage = lastMessage.apply { isPending = false }
            _messages.removeLast()
            _messages.add(newMessage)
        }
    }
}
