package com.example.simplechat.domain.repository

import com.example.simplechat.domain.model.ChatMessage

interface ChatRepository {
    suspend fun sendPrompt(
        apiKey: String,
        folderId: String,
        systemPrompt: String?,
        requestJson: Boolean,
        temperature: Double,
        history: List<ChatMessage>
    ): Result<ChatMessage>
}
