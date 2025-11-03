package com.example.simplechat.domain.repository

import com.example.simplechat.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun sendPrompt(apiKey: String, folderId: String, prompt: String): Flow<Result<ChatMessage>>
}
