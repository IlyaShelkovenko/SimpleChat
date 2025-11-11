package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.repository.SettingsRepository

class SaveApiCredentialsUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(apiKey: String) {
        settingsRepository.saveApiKey(apiKey)
    }
}
