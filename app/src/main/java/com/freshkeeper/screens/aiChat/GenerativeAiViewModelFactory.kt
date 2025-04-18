package com.freshkeeper.screens.aiChat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.freshkeeper.BuildConfig
import com.freshkeeper.screens.aiChat.viewmodel.ChatViewModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig

val GenerativeViewModelFactory =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras,
        ): T {
            val config =
                generationConfig {
                    temperature = 0.5f
                }

            val harassmentSafety =
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.LOW_AND_ABOVE)

            val hateSpeechSafety =
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.LOW_AND_ABOVE)

            val sexualSafety =
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.LOW_AND_ABOVE)

            val dangerousContentSafety =
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.LOW_AND_ABOVE)

            @Suppress("UNCHECKED_CAST")
            return when {
                modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                    val generativeModel =
                        GenerativeModel(
                            modelName = "gemini-2.0-flash",
                            apiKey = BuildConfig.API_KEY,
                            generationConfig = config,
                            safetySettings =
                                listOf(
                                    harassmentSafety,
                                    hateSpeechSafety,
                                    sexualSafety,
                                    dangerousContentSafety,
                                ),
                        )
                    ChatViewModel(generativeModel) as T
                }

                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
