package com.example.simplechat.data.repository

import com.example.simplechat.data.network.ChatCompletionService
import com.example.simplechat.data.network.model.ChatCompletionMessageDto
import com.example.simplechat.domain.model.ChatMessage
import com.example.simplechat.domain.model.MessageRole
import com.example.simplechat.domain.repository.ChatRepository

class ChatRepositoryImpl(
    private val apiService: ChatCompletionService
) : ChatRepository {
    override suspend fun sendPrompt(
        apiKey: String,
        systemPrompt: String?,
        requestJson: Boolean,
        temperature: Double,
        model: String,
        history: List<ChatMessage>
    ): Result<ChatMessage> = runCatching {
        val conversation = history.mapNotNull { message ->
            message.toDto()
        }
        val response = apiService.sendPrompt(
            apiKey = apiKey,
            folderId = null,
            systemPrompt = systemPrompt,
            requestJson = requestJson,
            temperature = temperature,
            model = model,
            messages = conversation
        )
        val content = response.choices.firstOrNull()?.message?.content
            ?: throw IllegalStateException("Empty response from assistant")
        ChatMessage(
            role = MessageRole.ASSISTANT,
            content = content.trim()
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
}
