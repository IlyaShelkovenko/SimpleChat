package com.example.simplechat.data.network

import com.example.simplechat.data.network.model.ChatCompletionMessageDto
import com.example.simplechat.data.network.model.ChatCompletionResponseDto

interface ChatCompletionService {
    suspend fun sendPrompt(
        apiKey: String,
        folderId: String?,
        systemPrompt: String?,
        requestJson: Boolean,
        temperature: Double,
        model: String,
        messages: List<ChatCompletionMessageDto>
    ): ChatCompletionResponseDto
}
