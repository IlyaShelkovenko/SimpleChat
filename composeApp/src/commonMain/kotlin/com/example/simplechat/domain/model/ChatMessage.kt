package com.example.simplechat.domain.model

import kotlin.time.Clock.*
import kotlin.time.ExperimentalTime

enum class MessageRole {
    USER,
    ASSISTANT
}

data class ChatMessage @OptIn(ExperimentalTime::class) constructor(
    val id: String = "msg_${System.now().toEpochMilliseconds()}",
    val role: MessageRole,
    val content: String,
    val createdAt: Long = System.now().toEpochMilliseconds(),
    val promptTokens: Int? = null,
    val completionTokens: Int? = null,
    val totalTokens: Int? = null
)
