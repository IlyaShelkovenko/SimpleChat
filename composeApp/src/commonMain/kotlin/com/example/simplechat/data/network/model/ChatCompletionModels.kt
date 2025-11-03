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
    val maxTokens: Int? = null
)

@Serializable
data class YandexCompletionRequest(
    val modelUri: String,
    val completionOptions: YandexCompletionOptions,
    val messages: List<YandexMessageDto>
)

@Serializable
data class YandexCompletionResponse(
    val result: Result? = null
) {
    @Serializable
    data class Result(
        val alternatives: List<Alternative> = emptyList(),
        val usage: Usage? = null,
        val modelVersion: String? = null
    )

    @Serializable
    data class Alternative(
        val message: YandexMessageDto? = null,
        @SerialName("status") val status: String? = null
    )

    @Serializable
    data class Usage(
        val inputTextTokens: Int? = null,
        val completionTokens: Int? = null,
        val totalTokens: Int? = null,
        val completionTokensDetails: CompletionTokensDetails? = null
    )

    @Serializable
    data class CompletionTokensDetails(
        val reasoningTokens: Int? = null
    )
}
