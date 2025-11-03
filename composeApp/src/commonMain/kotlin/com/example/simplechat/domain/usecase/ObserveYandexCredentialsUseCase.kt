package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.model.YandexCredentials
import com.example.simplechat.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class ObserveYandexCredentialsUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<YandexCredentials?> = settingsRepository.observeCredentials()
}
