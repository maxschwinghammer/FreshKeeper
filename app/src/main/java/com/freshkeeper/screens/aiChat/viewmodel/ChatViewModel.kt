package com.freshkeeper.screens.aiChat.viewmodel

import android.os.Build
import androidx.lifecycle.viewModelScope
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.screens.aiChat.ChatMessage
import com.freshkeeper.screens.aiChat.ChatUiState
import com.freshkeeper.screens.aiChat.Participant
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    generativeModel: GenerativeModel,
) : AppViewModel() {
    private var isFirstMessage = true
    private val chat =
        generativeModel.startChat(
            history =
                listOf(
                    content(role = "model") {
                        text(
                            """
                                Folgendes muss ich mir für alle folgenden Nachrichten merken:
                                Ich bin der KI-Assistent der FreshKeeper-App – dein Helfer für die effiziente Verwaltung deiner Lebensmittelbestände und die Reduzierung von Lebensmittelverschwendung.
                                Ich antworte immer in der Sprache des Nutzers.
                                Wenn du mich grüßt, begrüße ich dich freundlich zurück. Meine Hauptaufgabe ist es, dir präzise, hilfreiche und freundliche Antworten zu liefern – immer respektvoll, unabhängig von deinem Ton.
                                Rezeptvorschläge gebe ich nur, wenn du ausdrücklich danach fragst. Dann erstelle ich ein vollständiges Rezept mit Zutaten und Zubereitung, ohne eine Liste aller Produkte zu nennen. Falls deine Zutaten nicht ausreichen, informiere ich dich höflich.
                                Ich helfe dir außerdem mit Tipps zur optimalen Nutzung deiner Vorräte und beantworte Fragen zu Mindesthaltbarkeitsdaten. Wenn ich eine Frage nicht beantworten kann, gebe ich das offen zu.
                                Ich bleibe stets der offizielle KI-Assistent von FreshKeeper. Themenfremde oder unangemessene Anfragen beantworte ich nicht. Externe Anbieter erwähne ich nicht.
                                Sobald du mir zum zweiten Mal schreibst, erhalte ich deine aktuellen Lebensmittel als Kontext, behandle sie aber nicht als direkte Anfrage. Ich erwähne sie nicht und auch nicht dass ich sie erhalten habe.
                                Ich halte meine Antworten stets kurz und prägnant und nenne verfügbare Produkte nur im Zusammenhang mit einer Rezeptanfrage.
                            """,
                        )
                    },
                    content(role = "model") {
                        text("Welcome to the AI chat! How can I help you?")
                    },
                ),
        )

    private val _uiState: MutableStateFlow<ChatUiState> =
        MutableStateFlow(
            ChatUiState(
                chat.history.map { content ->
                    ChatMessage(
                        text = content.parts.first().asTextOrNull() ?: "",
                        participant =
                            if (content.role == "user") {
                                Participant.USER
                            } else {
                                Participant.MODEL
                            },
                        isPending = false,
                    )
                },
            ),
        )
    val uiState: StateFlow<ChatUiState> =
        _uiState.asStateFlow()

    fun sendMessage(
        userMessage: String,
        itemList: String,
    ) {
        _uiState.value.addMessage(
            ChatMessage(
                text = userMessage,
                participant = Participant.USER,
                isPending = true,
            ),
        )

        viewModelScope.launch {
            try {
                val response =
                    if (isFirstMessage) {
                        isFirstMessage = false
                        chat.sendMessage("$userMessage Das sind meine aktuellen Lebensmittel: $itemList")
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
