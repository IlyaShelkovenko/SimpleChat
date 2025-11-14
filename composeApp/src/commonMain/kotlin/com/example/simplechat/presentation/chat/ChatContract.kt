package com.example.simplechat.presentation.chat

import com.example.simplechat.domain.model.ChatMessage

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val prompt: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val totalTokensUsed: Int = 0
)

sealed interface ChatEvent {
    data class PromptChanged(val value: String) : ChatEvent
    data object SubmitPrompt : ChatEvent
    data object ClearChat : ChatEvent
}

sealed interface ChatEffect {
    data class ShowError(val message: String) : ChatEffect
    data class ShowResponseInfo(val message: String) : ChatEffect
    data class ShowCompressionInfo(val message: String) : ChatEffect
}
