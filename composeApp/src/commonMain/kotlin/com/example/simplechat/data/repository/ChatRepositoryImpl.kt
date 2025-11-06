package com.example.simplechat.data.repository

import com.example.simplechat.data.network.ChatApiService
import com.example.simplechat.data.network.model.YandexMessageDto
import com.example.simplechat.domain.model.ChatMessage
import com.example.simplechat.domain.model.MessageRole
import com.example.simplechat.domain.repository.ChatRepository

class ChatRepositoryImpl(
    private val apiService: ChatApiService
) : ChatRepository {
    override suspend fun sendPrompt(
        apiKey: String,
        folderId: String,
        systemPrompt: String?,
        requestJson: Boolean,
        history: List<ChatMessage>
    ): Result<ChatMessage> = runCatching {
        val conversation = buildList {
            history.forEach { message ->
                message.toYandexMessageDto()?.let { add(it) }
            }
        }
        val response = apiService.sendPrompt(
            apiKey = apiKey,
            folderId = folderId,
            systemPrompt = systemPrompt,
            requestJson = requestJson,
            messages = conversation
        )
        val content = response.result?.alternatives?.firstOrNull()?.message?.text
            ?: throw IllegalStateException("Empty response from assistant")
        ChatMessage(
            role = MessageRole.ASSISTANT,
            content = content.trim()
        )
    }

    private fun ChatMessage.toYandexMessageDto(): YandexMessageDto? {
        if (content.isBlank()) return null
        val roleName = when (role) {
            MessageRole.USER -> "user"
            MessageRole.ASSISTANT -> "assistant"
        }
        return YandexMessageDto(role = roleName, text = content)
    }
}
