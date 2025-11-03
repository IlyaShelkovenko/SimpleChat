package com.example.simplechat.data.repository

import com.example.simplechat.data.network.ChatApiService
import com.example.simplechat.domain.model.ChatMessage
import com.example.simplechat.domain.model.MessageRole
import com.example.simplechat.domain.repository.ChatRepository

class ChatRepositoryImpl(
    private val apiService: ChatApiService
) : ChatRepository {
    override suspend fun sendPrompt(apiKey: String, folderId: String, prompt: String): Result<ChatMessage> = runCatching {
        val response = apiService.sendPrompt(apiKey, folderId, prompt)
        val content = response.alternatives.firstOrNull()?.message?.text
            ?: throw IllegalStateException("Empty response from assistant")
        ChatMessage(
            role = MessageRole.ASSISTANT,
            content = content.trim()
        )
    }
}
