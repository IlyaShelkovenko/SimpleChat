package com.example.simplechat.data.network

import com.example.simplechat.data.network.model.ChatCompletionChoiceDto
import com.example.simplechat.data.network.model.ChatCompletionMessageDto
import com.example.simplechat.data.network.model.ChatCompletionResponseDto
import com.example.simplechat.data.network.model.YandexMessageDto

class YandexChatCompletionService(
    private val delegate: ChatApiService
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
        require(!folderId.isNullOrBlank()) { "Folder ID is required for Yandex chat service" }
        val yandexMessages = messages.map { message ->
            YandexMessageDto(role = message.role, text = message.content)
        }
        val response = delegate.sendPrompt(
            apiKey = apiKey,
            folderId = folderId,
            systemPrompt = systemPrompt,
            requestJson = requestJson,
            temperature = temperature,
            messages = yandexMessages
        )
        val content = response.result?.alternatives?.firstOrNull()?.message?.text
        val choice = ChatCompletionChoiceDto(
            message = content?.let { text ->
                ChatCompletionMessageDto(role = "assistant", content = text)
            }
        )
        return ChatCompletionResponseDto(
            choices = listOfNotNull(choice)
        )
    }
}
