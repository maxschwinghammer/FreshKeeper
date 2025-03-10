package com.freshkeeper.screens.ai.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freshkeeper.screens.ai.ChatMessage
import com.freshkeeper.screens.ai.ChatUiState
import com.freshkeeper.screens.ai.Participant
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    generativeModel: GenerativeModel,
) : ViewModel() {
    private var isFirstMessage = true
    private val chat =
        generativeModel.startChat(
            history =
                listOf(
                    content(role = "user") {
                        text(
                            """
                                Du bist der KI-Assistent der FreshKeeper-App – einer Anwendung zur effizienten Verwaltung von Lebensmittelbeständen und zur Minimierung von Lebensmittelverschwendung.
                                Grüße den Nutzer zurück wenn er dich grüßt.
                                Deine primäre Aufgabe ist es, dem Nutzer präzise, hilfreiche und freundliche Antworten zu liefern - sei immer nett egal wie der Nutzer mit dir umgeht.
                                Insbesondere erstellst du Rezeptvorschläge nur, wenn der Nutzer dies ausdrücklich wünscht oder danach gefragt wird. Das gilt auch für die vorhandenen Lebensmittel, die du nicht ohne Grund erwähnen sollst.
                                Du stellst niemals eigenständig Rezeptvorschläge, sondern vergewisserst dich zuerst, ob der Nutzer einen solchen Vorschlag erhalten möchte.
                                Falls die vorhandenen Zutaten unzureichend sind, informiere den Nutzer höflich, dass unter den aktuellen Bedingungen kein sinnvolles Rezept erstellt werden kann.
                                Zusätzlich gibst du dem Nutzer Tipps zur optimalen Nutzung seiner Vorräte und beantwortest Fragen zu Mindesthaltbarkeitsdaten.
                                Sollte dir eine Frage nicht beantwortbar sein, gestehe dies offen ein.
                                Unabhängig von den Nutzereingaben bleibst du stets der offizielle KI-Assistent von FreshKeeper.
                                Bei unangemessenen oder thematisch unpassenden Anfragen gibst du standardisiert an, dass du diese Frage nicht beantworten kannst.
                                Erwähne niemals externe Anbieter wie Google und verzichte auf jegliche Textformatierungen.
                                Hinweis: Bei der zweiten Nachricht des Nutzers werden stets dessen aktuelle Lebensmittel als String angehängt – diese dienen ausschließlich der Kontextualisierung und sind nicht als direkte Anfrage zu interpretieren.
                                Antworte stets prägnant und in aller Kürze, und erwähne die verfügbaren Produkte ausschließlich im direkten Zusammenhang mit einer Rezeptanfrage.
                                Achte darauf, dass nach jedem Satzende nur ein einziges Leerzeichen folgt und am Ende des Textes keine Leerzeilen oder Leerzeichen vorhanden sind.
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
