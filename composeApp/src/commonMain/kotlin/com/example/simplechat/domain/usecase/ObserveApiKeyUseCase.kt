package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class ObserveApiKeyUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<String?> = settingsRepository.observeApiKey()
}
