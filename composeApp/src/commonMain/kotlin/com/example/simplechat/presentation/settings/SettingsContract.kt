package com.example.simplechat.presentation.settings

import com.example.simplechat.domain.model.AssistantSettings

data class SettingsUiState(
    val apiKey: String = "",
    val availableModels: List<String> = listOf(AssistantSettings.DEFAULT_MODEL),
    val selectedModel: String = AssistantSettings.DEFAULT_MODEL,
    val customSystemPrompt: String = "",
    val isCustomPromptEnabled: Boolean = false,
    val isJsonFormatEnabled: Boolean = false,
    val temperature: Double = 0.7,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

sealed interface SettingsEvent {
    data class ApiKeyChanged(val value: String) : SettingsEvent
    data class ModelChanged(val value: String) : SettingsEvent
    data class CustomSystemPromptChanged(val value: String) : SettingsEvent
    data class CustomSystemPromptEnabledChanged(val enabled: Boolean) : SettingsEvent
    data class JsonFormatEnabledChanged(val enabled: Boolean) : SettingsEvent
    data class TemperatureChanged(val value: Double) : SettingsEvent
    data object Save : SettingsEvent
}
