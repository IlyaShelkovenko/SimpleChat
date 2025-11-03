package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.model.ChatMessage
import com.example.simplechat.domain.repository.ChatRepository
import com.example.simplechat.domain.repository.SettingsRepository

class SendPromptUseCase(
    private val chatRepository: ChatRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(prompt: String): Result<ChatMessage> {
        val credentials = settingsRepository.getCredentials()
            ?: return Result.failure(IllegalStateException("API key or folder ID missing"))
        if (credentials.apiKey.isBlank() || credentials.folderId.isBlank()) {
            return Result.failure(IllegalStateException("API key or folder ID missing"))
        }
        return chatRepository.sendPrompt(credentials.apiKey, credentials.folderId, prompt)
    }
}
