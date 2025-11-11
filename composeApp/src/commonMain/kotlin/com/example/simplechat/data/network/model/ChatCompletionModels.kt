package com.example.simplechat.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionMessageDto(
    val role: String,
    val content: String
)

@Serializable
data class ChatCompletionResponseFormatDto(
    val type: String
)

@Serializable
data class ChatCompletionRequestDto(
    val model: String,
    val messages: List<ChatCompletionMessageDto>,
    val temperature: Double,
    @SerialName("response_format")
    val responseFormat: ChatCompletionResponseFormatDto? = null
)

@Serializable
data class ChatCompletionChoiceDto(
    val index: Int? = null,
    val message: ChatCompletionMessageDto? = null,
    @SerialName("finish_reason")
    val finishReason: String? = null
)

@Serializable
data class ChatCompletionResponseDto(
    val id: String? = null,
    val `object`: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<ChatCompletionChoiceDto> = emptyList(),
    val usage: ChatCompletionUsageDto? = null
)

@Serializable
data class ChatCompletionUsageDto(
    @SerialName("prompt_tokens")
    val promptTokens: Int? = null,
    @SerialName("completion_tokens")
    val completionTokens: Int? = null,
    @SerialName("total_tokens")
    val totalTokens: Int? = null
)

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
    val messages: List<YandexMessageDto>,
    @SerialName("json_object")
    val jsonObject: Boolean? = null
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
