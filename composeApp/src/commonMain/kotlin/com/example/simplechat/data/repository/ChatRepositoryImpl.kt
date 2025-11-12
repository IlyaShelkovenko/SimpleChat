package com.example.simplechat.data.repository

import com.example.simplechat.data.network.ChatCompletionService
import com.example.simplechat.data.network.model.ChatCompletionMessageDto
import com.example.simplechat.domain.model.ChatMessage
import com.example.simplechat.domain.model.ChatResponse
import com.example.simplechat.domain.model.MessageRole
import com.example.simplechat.domain.repository.ChatRepository
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

class ChatRepositoryImpl(
    private val apiService: ChatCompletionService
) : ChatRepository {
    @OptIn(ExperimentalTime::class)
    override suspend fun sendPrompt(
        apiKey: String,
        systemPrompt: String?,
        requestJson: Boolean,
        temperature: Double,
        model: String,
        history: List<ChatMessage>
    ): Result<ChatResponse> = runCatching {
        val conversation = history.mapNotNull { message ->
            message.toDto()
        }
        val mark = TimeSource.Monotonic.markNow()
        val response = apiService.sendPrompt(
            apiKey = apiKey,
            folderId = null,
            systemPrompt = systemPrompt,
            requestJson = requestJson,
            temperature = temperature,
            model = model,
            messages = conversation
        )
        val durationMillis = mark.elapsedNow().inWholeMilliseconds
        val content = response.choices.firstOrNull()?.message?.content
            ?: throw IllegalStateException("Empty response from assistant")
        val usage = response.usage
        val trimmedContent = content.trim()
        val completionTokens = usage?.completionTokens ?: estimateTokens(trimmedContent)
        val promptTokens = usage?.promptTokens
        val totalTokens = usage?.totalTokens
        val assistantMessage = ChatMessage(
            role = MessageRole.ASSISTANT,
            content = trimmedContent,
            promptTokens = promptTokens,
            completionTokens = completionTokens,
            totalTokens = totalTokens
        )
        ChatResponse(
            message = assistantMessage,
            durationMillis = durationMillis,
            promptTokens = promptTokens,
            completionTokens = completionTokens,
            totalTokens = totalTokens
        )
    }

    private fun ChatMessage.toDto(): ChatCompletionMessageDto? {
        if (content.isBlank()) return null
        val roleName = when (role) {
            MessageRole.USER -> "user"
            MessageRole.ASSISTANT -> "assistant"
        }
        return ChatCompletionMessageDto(role = roleName, content = content)
    }

    private fun estimateTokens(content: String): Int {
        if (content.isBlank()) return 0
        return content.trim().split(Regex("\\s+")).size
    }
}
