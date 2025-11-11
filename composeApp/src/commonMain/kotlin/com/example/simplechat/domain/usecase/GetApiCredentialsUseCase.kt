package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.model.ApiCredentials
import com.example.simplechat.domain.repository.SettingsRepository

class GetApiCredentialsUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): ApiCredentials? = settingsRepository.getApiCredentials()
}
