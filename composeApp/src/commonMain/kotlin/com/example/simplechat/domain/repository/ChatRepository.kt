package com.example.simplechat.domain.repository

import com.example.simplechat.domain.model.ChatMessage

interface ChatRepository {
    suspend fun sendPrompt(
        apiKey: String,
        systemPrompt: String?,
        requestJson: Boolean,
        temperature: Double,
        model: String,
        history: List<ChatMessage>
    ): Result<ChatMessage>
}
