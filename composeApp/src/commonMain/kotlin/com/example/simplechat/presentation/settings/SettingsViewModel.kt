package com.example.simplechat.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplechat.domain.usecase.ObserveAssistantSettingsUseCase
import com.example.simplechat.domain.usecase.SaveAssistantSettingsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val observeAssistantSettingsUseCase: ObserveAssistantSettingsUseCase,
    private val saveAssistantSettingsUseCase: SaveAssistantSettingsUseCase
) : ViewModel() {

    private var observeJob: Job? = null
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeAssistantSettings()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.CustomSystemPromptChanged -> updateState {
                copy(customSystemPrompt = event.value, isSaved = false)
            }

            is SettingsEvent.CustomSystemPromptEnabledChanged -> updateState {
                copy(
                    isCustomPromptEnabled = event.enabled,
                    isJsonFormatEnabled = if (event.enabled) false else isJsonFormatEnabled,
                    isSaved = false
                )
            }

            is SettingsEvent.JsonFormatEnabledChanged -> updateState {
                copy(
                    isJsonFormatEnabled = event.enabled,
                    isCustomPromptEnabled = if (event.enabled) false else isCustomPromptEnabled,
                    isSaved = false
                )
            }

            is SettingsEvent.TemperatureChanged -> updateState {
                copy(
                    temperature = event.value,
                    isSaved = false
                )
            }

            SettingsEvent.Save -> saveSettings()
        }
    }

    private fun observeAssistantSettings() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            observeAssistantSettingsUseCase().collectLatest { settings ->
                updateState {
                    copy(
                        customSystemPrompt = settings.customSystemPrompt,
                        isCustomPromptEnabled = settings.isCustomPromptEnabled,
                        isJsonFormatEnabled = settings.isJsonFormatEnabled,
                        temperature = settings.temperature
                    )
                }
            }
        }
    }

    private fun saveSettings() {
        val currentState = _uiState.value
        if (currentState.isSaving) return
        updateState { copy(isSaving = true, isSaved = false) }
        viewModelScope.launch {
            saveAssistantSettingsUseCase(
                useCustomSystemPrompt = currentState.isCustomPromptEnabled,
                customSystemPrompt = currentState.customSystemPrompt,
                useJsonFormat = currentState.isJsonFormatEnabled,
                temperature = currentState.temperature
            )
            updateState { copy(isSaving = false, isSaved = true) }
        }
    }

    private fun updateState(reducer: SettingsUiState.() -> SettingsUiState) {
        _uiState.value = _uiState.value.reducer()
    }
}
