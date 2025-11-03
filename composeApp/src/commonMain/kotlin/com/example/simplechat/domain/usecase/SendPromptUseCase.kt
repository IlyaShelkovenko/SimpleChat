package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.model.ChatMessage
import com.example.simplechat.domain.repository.ChatRepository
import com.example.simplechat.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class SendPromptUseCase(
    private val chatRepository: ChatRepository,
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(prompt: String): Flow<Result<ChatMessage>> = flow {
        val credentials = settingsRepository.getCredentials()
        if (credentials == null || credentials.apiKey.isBlank() || credentials.folderId.isBlank()) {
            emit(Result.failure(IllegalStateException("API key or folder ID missing")))
            return@flow
        }

        emitAll(chatRepository.sendPrompt(credentials.apiKey, credentials.folderId, prompt))
    }
}
