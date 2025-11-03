package com.example.simplechat.data.network

import com.example.simplechat.data.network.model.YandexCompletionOptions
import com.example.simplechat.data.network.model.YandexCompletionRequest
import com.example.simplechat.data.network.model.YandexCompletionResponse
import com.example.simplechat.data.network.model.YandexMessageDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private const val DEFAULT_SYSTEM_PROMPT = "You are a helpful assistant."

class ChatApiService(
    private val client: HttpClient,
    private val baseUrl: String = "https://llm.api.cloud.yandex.net/foundationModels/v1/completion",
    private val model: String = "yandexgpt-lite"
) {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend fun sendPrompt(apiKey: String, folderId: String, prompt: String): YandexCompletionResponse {
        val request = YandexCompletionRequest(
            modelUri = "gpt://$folderId/$model/latest",
            completionOptions = YandexCompletionOptions(
                stream = false,
                temperature = 0.7,
                maxTokens = null
            ),
            messages = listOf(
                YandexMessageDto(role = "system", text = DEFAULT_SYSTEM_PROMPT),
                YandexMessageDto(role = "user", text = prompt)
            )
        )

        return client.post(baseUrl) {
            header(HttpHeaders.Authorization, "Api-Key $apiKey")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    fun streamPrompt(apiKey: String, folderId: String, prompt: String): Flow<String> = flow {
        val request = YandexCompletionRequest(
            modelUri = "gpt://$folderId/$model/latest",
            completionOptions = YandexCompletionOptions(
                stream = true,
                temperature = 0.7,
                maxTokens = null
            ),
            messages = listOf(
                YandexMessageDto(role = "system", text = DEFAULT_SYSTEM_PROMPT),
                YandexMessageDto(role = "user", text = prompt)
            )
        )

        var lastEmitted = ""
        val aggregated = StringBuilder()

        client.preparePost(baseUrl) {
            header(HttpHeaders.Authorization, "Api-Key $apiKey")
            contentType(ContentType.Application.Json)
            accept(ContentType.Text.EventStream)
            setBody(request)
        }.execute { response ->
            val channel = response.bodyAsChannel()
            val eventBuffer = StringBuilder()
            suspend fun processPayload(payload: String): Boolean {
                if (payload.isEmpty()) return false
                if (payload == "[DONE]") return true

                val chunk = try {
                    json.decodeFromString<YandexCompletionResponse>(payload)
                } catch (_: SerializationException) {
                    return false
                }

                val alternative = chunk.result?.alternatives?.firstOrNull()
                val text = alternative?.message?.text ?: return false
                if (text.isEmpty()) return false

                val candidate = if (text.startsWith(lastEmitted) && text.length >= lastEmitted.length) {
                    aggregated.clear()
                    aggregated.append(text)
                    text
                } else {
                    aggregated.append(text)
                    aggregated.toString()
                }

                if (candidate != lastEmitted) {
                    lastEmitted = candidate
                    emit(candidate)
                }
                return false
            }

            while (true) {
                val line = channel.readUTF8Line() ?: break

                if (line.isBlank()) {
                    val payload = eventBuffer.toString().trim()
                    eventBuffer.clear()
                    if (processPayload(payload)) break
                    continue
                }

                if (line.startsWith("data:")) {
                    if (eventBuffer.isNotEmpty()) {
                        eventBuffer.append('\n')
                    }
                    eventBuffer.append(line.removePrefix("data:").trimStart())
                }
            }

            if (eventBuffer.isNotEmpty()) {
                processPayload(eventBuffer.toString().trim())
            }
        }
    }
}
