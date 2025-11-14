package com.example.simplechat.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplechat.domain.model.ChatMessage
import com.example.simplechat.domain.model.ChatResponse
import com.example.simplechat.domain.model.MessageRole
import com.example.simplechat.domain.usecase.SendPromptUseCase
import kotlin.collections.buildList
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

    private var compressedSummary: ChatMessage? = null
    private var compressionCyclesCompleted: Int = 0
    private var compressionInProgress: Boolean = false

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
        val summaryMessage = compressedSummary
        val historyForRequest = buildList {
            summaryMessage?.let { add(it) }
            addAll(updatedHistory)
        }
        _uiState.value = _uiState.value.copy(
            messages = updatedHistory,
            prompt = "",
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            sendPromptUseCase(historyForRequest)
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

                    val conversation = updatedMessages + assistantMessage
                    _uiState.value = _uiState.value.copy(
                        messages = conversation,
                        isLoading = false,
                        totalTokensUsed = _uiState.value.totalTokensUsed + totalTokens
                    )
                    emitResponseInfo(responseForInfo)
                    maybeCompressConversation(conversation)
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
        compressedSummary = null
        compressionCyclesCompleted = 0
        compressionInProgress = false
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

    private fun emitCompressionHandled(summaryPreview: String) {
        val message = if (summaryPreview.isBlank()) {
            "Conversation summary has been updated"
        } else {
            val preview = summaryPreview.take(80)
            "Conversation compressed: $preview" + if (summaryPreview.length > 80) "…" else ""
        }
        viewModelScope.launch {
            _effects.send(ChatEffect.ShowCompressionInfo(message))
        }
    }

    private fun maybeCompressConversation(messages: List<ChatMessage>) {
        if (compressionInProgress) return
        val requiredCycles = messages.size / COMPRESSION_INTERVAL
        if (requiredCycles <= compressionCyclesCompleted || messages.isEmpty()) {
            return
        }

        compressionInProgress = true
        viewModelScope.launch {
            try {
                val summaryPrompt = ChatMessage(
                    role = MessageRole.USER,
                    content = COMPRESSION_PROMPT
                )
                val historyForCompression = buildList {
                    compressedSummary?.let { add(it) }
                    addAll(messages)
                    add(summaryPrompt)
                }

                sendPromptUseCase(historyForCompression)
                    .onSuccess { response ->
                        val summaryMessage = response.message.copy(
                            content = response.message.content.trim()
                        )
                        compressedSummary = summaryMessage
                        compressionCyclesCompleted = requiredCycles

                        val promptTokens = response.promptTokens ?: estimateTokens(summaryPrompt.content)
                        val completionTokens = response.completionTokens ?: estimateTokens(summaryMessage.content)
                        val totalTokens = response.totalTokens ?: (promptTokens + completionTokens)

                        _uiState.value = _uiState.value.copy(
                            totalTokensUsed = _uiState.value.totalTokensUsed + totalTokens
                        )
                        emitCompressionHandled(summaryMessage.content)
                    }
                    .onFailure { error ->
                        emitError(error.message ?: "Unable to compress conversation")
                    }
            } finally {
                compressionInProgress = false
            }
        }
    }

    private fun estimateTokens(content: String): Int {
        if (content.isBlank()) return 0
        return content.trim().split(Regex("\\s+")).size
    }

    private companion object {
        const val COMPRESSION_INTERVAL = 10
        const val COMPRESSION_PROMPT =
            "summarize this dialog messages and compress it, leave all important information " +
                "and vector of main goal of this dialog"
    }
}
