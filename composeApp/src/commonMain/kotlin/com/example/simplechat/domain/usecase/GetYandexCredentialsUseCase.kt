package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.model.YandexCredentials
import com.example.simplechat.domain.repository.SettingsRepository

class GetYandexCredentialsUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): YandexCredentials? = settingsRepository.getCredentials()
}
