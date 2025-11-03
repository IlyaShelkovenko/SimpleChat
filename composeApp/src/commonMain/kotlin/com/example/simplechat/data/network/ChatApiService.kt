package com.example.simplechat.data.network

import com.example.simplechat.data.network.model.ChatCompletionRequest
import com.example.simplechat.data.network.model.ChatCompletionResponse
import com.example.simplechat.data.network.model.ChatMessageDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class ChatApiService(
    private val client: HttpClient,
    private val baseUrl: String = "https://api.openai.com/v1/chat/completions",
    private val model: String = "gpt-3.5-turbo"
) {
    suspend fun sendPrompt(apiKey: String, prompt: String): ChatCompletionResponse {
        val request = ChatCompletionRequest(
            model = model,
            messages = listOf(
                ChatMessageDto(role = "user", content = prompt)
            )
        )

        return client.post(baseUrl) {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
