package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.model.ChatMessage
import com.example.simplechat.domain.model.ChatResponse
import com.example.simplechat.domain.repository.ChatRepository
import com.example.simplechat.domain.repository.SettingsRepository

class SendPromptUseCase(
    private val chatRepository: ChatRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(history: List<ChatMessage>): Result<ChatResponse> {
        if (history.isEmpty()) {
            return Result.failure(IllegalArgumentException("Conversation history is empty"))
        }
        val credentials = settingsRepository.getApiCredentials()
            ?: return Result.failure(IllegalStateException("API key is missing"))
        if (credentials.apiKey.isBlank()) {
            return Result.failure(IllegalStateException("API key is missing"))
        }
        val assistantSettings = settingsRepository.getAssistantSettings()
        val systemPrompt = if (assistantSettings.isCustomPromptEnabled) {
            assistantSettings.customSystemPrompt.takeIf { it.isNotBlank() }
        } else {
            null
        }
        return chatRepository.sendPrompt(
            apiKey = credentials.apiKey,
            systemPrompt = systemPrompt,
            requestJson = assistantSettings.isJsonFormatEnabled,
            temperature = assistantSettings.temperature,
            model = assistantSettings.model,
            history = history
        )
    }
}
