package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.model.AssistantSettings
import com.example.simplechat.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class ObserveAssistantSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<AssistantSettings> = settingsRepository.observeAssistantSettings()
}
