package com.example.simplechat.domain.model

data class ChatResponse(
    val message: ChatMessage,
    val durationMillis: Long,
    val completionTokens: Int?
)
