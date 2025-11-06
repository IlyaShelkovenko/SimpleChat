package com.example.simplechat.data.network

import com.example.simplechat.data.network.model.YandexCompletionOptions
import com.example.simplechat.data.network.model.YandexCompletionRequest
import com.example.simplechat.data.network.model.YandexCompletionResponse
import com.example.simplechat.data.network.model.YandexMessageDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

private const val DEFAULT_SYSTEM_PROMPT = "You are a helpful assistant."

class ChatApiService(
    private val client: HttpClient,
    private val baseUrl: String = "https://llm.api.cloud.yandex.net/foundationModels/v1/completion",
    private val model: String = "yandexgpt-lite"
) {
    suspend fun sendPrompt(
        apiKey: String,
        folderId: String,
        systemPrompt: String?,
        requestJson: Boolean,
        messages: List<YandexMessageDto>
    ): YandexCompletionResponse {
        val resolvedSystemPrompt = systemPrompt?.takeIf { it.isNotBlank() } ?: DEFAULT_SYSTEM_PROMPT
        val requestMessages = buildList {
            add(YandexMessageDto(role = "system", text = resolvedSystemPrompt))
            addAll(messages)
        }
        val request = YandexCompletionRequest(
            modelUri = "gpt://$folderId/$model/latest",
            completionOptions = YandexCompletionOptions(
                stream = false,
                temperature = 0.7,
                maxTokens = null
            ),
            messages = requestMessages,
            jsonObject = if (requestJson) true else null
        )

        return client.post(baseUrl) {
            header(HttpHeaders.Authorization, "Api-Key $apiKey")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
