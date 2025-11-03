package com.example.simplechat.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplechat.domain.model.ChatMessage
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
        }
    }

    private fun submitPrompt() {
        val prompt = _uiState.value.prompt.trim()
        if (prompt.isEmpty()) {
            emitError("Please enter a prompt")
            return
        }

        val userMessage = ChatMessage(
            role = MessageRole.USER,
            content = prompt
        )
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            prompt = "",
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            sendPromptUseCase(prompt)
                .onSuccess { assistantMessage ->
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + assistantMessage,
                        isLoading = false
                    )
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

    private fun emitError(message: String) {
        viewModelScope.launch {
            _effects.send(ChatEffect.ShowError(message))
        }
    }
}
