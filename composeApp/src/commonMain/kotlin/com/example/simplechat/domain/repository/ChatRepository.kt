package com.example.simplechat.domain.repository

import com.example.simplechat.domain.model.ChatMessage

interface ChatRepository {
    suspend fun sendPrompt(apiKey: String, prompt: String): Result<ChatMessage>
}
