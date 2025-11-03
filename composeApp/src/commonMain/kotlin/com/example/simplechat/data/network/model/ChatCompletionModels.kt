package com.example.simplechat.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class YandexMessageDto(
    val role: String,
    val text: String
)

@Serializable
data class YandexCompletionOptions(
    val stream: Boolean = false,
    val temperature: Double = 0.7,
    val maxTokens: String? = null
)

@Serializable
data class YandexCompletionRequest(
    val modelUri: String,
    val completionOptions: YandexCompletionOptions,
    val messages: List<YandexMessageDto>
)

@Serializable
data class YandexCompletionResponse(
    val alternatives: List<Alternative> = emptyList()
) {
    @Serializable
    data class Alternative(
        val message: YandexMessageDto? = null,
        @SerialName("status") val status: String? = null
    )
}
