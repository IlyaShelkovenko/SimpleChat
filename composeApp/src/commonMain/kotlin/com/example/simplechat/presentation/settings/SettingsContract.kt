package com.example.simplechat.presentation.settings

data class SettingsUiState(
    val customSystemPrompt: String = "",
    val isCustomPromptEnabled: Boolean = false,
    val isJsonFormatEnabled: Boolean = false,
    val temperature: Double = 0.7,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

sealed interface SettingsEvent {
    data class CustomSystemPromptChanged(val value: String) : SettingsEvent
    data class CustomSystemPromptEnabledChanged(val enabled: Boolean) : SettingsEvent
    data class JsonFormatEnabledChanged(val enabled: Boolean) : SettingsEvent
    data class TemperatureChanged(val value: Double) : SettingsEvent
    data object Save : SettingsEvent
}
