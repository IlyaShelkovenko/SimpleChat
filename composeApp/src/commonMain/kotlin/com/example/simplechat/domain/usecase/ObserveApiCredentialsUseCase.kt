package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.model.ApiCredentials
import com.example.simplechat.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class ObserveApiCredentialsUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<ApiCredentials?> = settingsRepository.observeApiCredentials()
}
