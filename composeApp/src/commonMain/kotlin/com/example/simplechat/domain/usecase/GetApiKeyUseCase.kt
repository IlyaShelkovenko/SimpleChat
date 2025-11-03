package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.repository.SettingsRepository

class GetApiKeyUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): String? = settingsRepository.getApiKey()
}
