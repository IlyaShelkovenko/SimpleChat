package com.example.simplechat.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessageDto>,
    @SerialName("max_tokens") val maxTokens: Int = 512,
    val temperature: Double = 0.7
)

@Serializable
data class ChatMessageDto(
    val role: String,
    val content: String
)

@Serializable
data class ChatCompletionResponse(
    val choices: List<ChatChoiceDto>
)

@Serializable
data class ChatChoiceDto(
    val index: Int,
    val message: ChatMessageDto
)
