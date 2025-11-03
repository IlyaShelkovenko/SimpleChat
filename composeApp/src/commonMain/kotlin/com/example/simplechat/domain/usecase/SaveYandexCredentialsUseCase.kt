package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.repository.SettingsRepository

class SaveYandexCredentialsUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(apiKey: String, folderId: String) {
        settingsRepository.saveCredentials(apiKey, folderId)
    }
}
