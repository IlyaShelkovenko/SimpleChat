package com.example.simplechat.data.network

import com.example.simplechat.data.network.model.ChatCompletionMessageDto
import com.example.simplechat.data.network.model.ChatCompletionRequestDto
import com.example.simplechat.data.network.model.ChatCompletionResponseDto
import com.example.simplechat.data.network.model.ChatCompletionResponseFormatDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

private const val DEFAULT_SYSTEM_PROMPT = "You are a helpful assistant."

class HuggingFaceChatApiService(
    private val client: HttpClient,
    private val baseUrl: String = "https://router.huggingface.co/v1/chat/completions"
) : ChatCompletionService {

    override suspend fun sendPrompt(
        apiKey: String,
        folderId: String?,
        systemPrompt: String?,
        requestJson: Boolean,
        temperature: Double,
        model: String,
        messages: List<ChatCompletionMessageDto>
    ): ChatCompletionResponseDto {
        val resolvedSystemPrompt = systemPrompt?.takeIf { it.isNotBlank() } ?: DEFAULT_SYSTEM_PROMPT
        val payloadMessages = buildList {
            add(ChatCompletionMessageDto(role = "system", content = resolvedSystemPrompt))
            addAll(messages)
        }
        val responseFormat = if (requestJson) {
            ChatCompletionResponseFormatDto(type = "json_object")
        } else {
            null
        }
        val requestBody = ChatCompletionRequestDto(
            model = model,
            messages = payloadMessages,
            temperature = temperature,
            responseFormat = responseFormat
        )

        return client.post(baseUrl) {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }
}
