package com.example.simplechat.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplechat.domain.model.ChatMessage
import com.example.simplechat.domain.model.ChatResponse
import com.example.simplechat.domain.model.MessageRole
import com.example.simplechat.domain.usecase.SendPromptUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val sendPromptUseCase: SendPromptUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _effects = Channel<ChatEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.PromptChanged -> _uiState.value = _uiState.value.copy(
                prompt = event.value,
                errorMessage = null
            )

            ChatEvent.SubmitPrompt -> submitPrompt()
            ChatEvent.ClearChat -> clearChat()
        }
    }

    private fun submitPrompt() {
        val prompt = _uiState.value.prompt.trim()
        if (prompt.isEmpty()) {
            emitError("Please enter a prompt")
            return
        }

        val history = _uiState.value.messages
        val userMessage = ChatMessage(
            role = MessageRole.USER,
            content = prompt
        )
        val updatedHistory = history + userMessage
        _uiState.value = _uiState.value.copy(
            messages = updatedHistory,
            prompt = "",
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            sendPromptUseCase(updatedHistory)
                .onSuccess { response ->
                    val promptTokens = response.promptTokens ?: estimateTokens(userMessage.content)
                    val completionTokens = response.completionTokens ?: estimateTokens(response.message.content)
                    val totalTokens = response.totalTokens ?: (promptTokens + completionTokens)

                    val updatedMessages = _uiState.value.messages.map { message ->
                        if (message.id == userMessage.id) {
                            message.copy(promptTokens = promptTokens)
                        } else {
                            message
                        }
                    }

                    val assistantMessage = response.message.copy(
                        promptTokens = promptTokens,
                        completionTokens = completionTokens,
                        totalTokens = totalTokens
                    )

                    val responseForInfo = response.copy(
                        message = assistantMessage,
                        promptTokens = promptTokens,
                        completionTokens = completionTokens,
                        totalTokens = totalTokens
                    )

                    _uiState.value = _uiState.value.copy(
                        messages = updatedMessages + assistantMessage,
                        isLoading = false,
                        totalTokensUsed = _uiState.value.totalTokensUsed + totalTokens
                    )
                    emitResponseInfo(responseForInfo)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                    emitError(error.message ?: "Unable to get response")
                }
        }
    }

    private fun clearChat() {
        _uiState.value = ChatUiState()
    }

    private fun emitError(message: String) {
        viewModelScope.launch {
            _effects.send(ChatEffect.ShowError(message))
        }
    }

    private fun emitResponseInfo(response: ChatResponse) {
        val tokens = response.completionTokens ?: 0
        val infoMessage = "Response time: ${response.durationMillis} ms · Tokens: $tokens"
        viewModelScope.launch {
            _effects.send(ChatEffect.ShowResponseInfo(infoMessage))
        }
    }

    private fun estimateTokens(content: String): Int {
        if (content.isBlank()) return 0
        return content.trim().split(Regex("\\s+")).size
    }
}
