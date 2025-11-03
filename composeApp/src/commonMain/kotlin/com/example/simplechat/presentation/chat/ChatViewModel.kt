package com.example.simplechat.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplechat.domain.model.ChatMessage
import com.example.simplechat.domain.model.MessageRole
import com.example.simplechat.domain.usecase.SendPromptUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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

        viewModelScope.launch submissionJob@{
            var assistantMessageId: String? = null
            var failureHandled = false

            try {
                sendPromptUseCase(prompt).collect { result ->
                    result.onSuccess { assistantMessage ->
                        assistantMessageId = assistantMessage.id
                        val currentMessages = _uiState.value.messages
                        val existingIndex = currentMessages.indexOfFirst { it.id == assistantMessage.id }
                        val updatedMessages = if (existingIndex == -1) {
                            currentMessages + assistantMessage
                        } else {
                            currentMessages.toMutableList().apply { this[existingIndex] = assistantMessage }
                        }

                        _uiState.value = _uiState.value.copy(
                            messages = updatedMessages,
                            errorMessage = null
                        )
                    }

                    result.onFailure { error ->
                        failureHandled = true
                        if (error is CancellationException) throw error

                        val cleanedMessages = assistantMessageId?.let { id ->
                            _uiState.value.messages.filterNot { it.id == id }
                        } ?: _uiState.value.messages

                        _uiState.value = _uiState.value.copy(
                            messages = cleanedMessages,
                            errorMessage = error.message
                        )

                        emitError(error.message ?: "Unable to get response")
                        return@submissionJob
                    }
                }
            } catch (error: Throwable) {
                if (error is CancellationException) throw error
                if (!failureHandled) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = error.message
                    )
                    emitError(error.message ?: "Unable to get response")
                }
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun emitError(message: String) {
        viewModelScope.launch {
            _effects.send(ChatEffect.ShowError(message))
        }
    }
}
