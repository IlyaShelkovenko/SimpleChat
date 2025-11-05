package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.repository.SettingsRepository

class SaveAssistantSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(
        useCustomSystemPrompt: Boolean,
        customSystemPrompt: String,
        useJsonFormat: Boolean
    ) {
        settingsRepository.saveAssistantSettings(
            useCustomSystemPrompt = useCustomSystemPrompt,
            customSystemPrompt = customSystemPrompt,
            useJsonFormat = useJsonFormat
        )
    }
}
