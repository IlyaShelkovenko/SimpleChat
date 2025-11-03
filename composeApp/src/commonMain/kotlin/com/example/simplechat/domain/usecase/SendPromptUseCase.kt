package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.model.ChatMessage
import com.example.simplechat.domain.repository.ChatRepository
import com.example.simplechat.domain.repository.SettingsRepository

class SendPromptUseCase(
    private val chatRepository: ChatRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(prompt: String): Result<ChatMessage> {
        val apiKey = settingsRepository.getApiKey()
            ?: return Result.failure(IllegalStateException("API key missing"))
        return chatRepository.sendPrompt(apiKey, prompt)
    }
}
