package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.repository.SettingsRepository

class SaveApiKeyUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(apiKey: String) {
        settingsRepository.saveApiKey(apiKey)
    }
}
