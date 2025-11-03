package com.example.simplechat.domain.model

import kotlinx.datetime.Clock

enum class MessageRole {
    USER,
    ASSISTANT
}

data class ChatMessage(
    val id: String = "msg_${Clock.System.now().toEpochMilliseconds()}",
    val role: MessageRole,
    val content: String,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)
