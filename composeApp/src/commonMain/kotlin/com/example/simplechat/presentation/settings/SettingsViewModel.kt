package com.example.simplechat.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplechat.domain.usecase.ObserveApiCredentialsUseCase
import com.example.simplechat.domain.usecase.ObserveAssistantSettingsUseCase
import com.example.simplechat.domain.usecase.SaveApiCredentialsUseCase
import com.example.simplechat.domain.usecase.SaveAssistantSettingsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val observeAssistantSettingsUseCase: ObserveAssistantSettingsUseCase,
    private val saveAssistantSettingsUseCase: SaveAssistantSettingsUseCase,
    private val observeApiCredentialsUseCase: ObserveApiCredentialsUseCase,
    private val saveApiCredentialsUseCase: SaveApiCredentialsUseCase
) : ViewModel() {

    private var observeAssistantJob: Job? = null
    private var observeCredentialsJob: Job? = null
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeAssistantSettings()
        observeCredentials()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ApiKeyChanged -> updateState {
                copy(apiKey = event.value, isSaved = false, errorMessage = null)
            }

            is SettingsEvent.ModelChanged -> updateState {
                copy(selectedModel = event.value, isSaved = false, errorMessage = null)
            }

            is SettingsEvent.CustomSystemPromptChanged -> updateState {
                copy(customSystemPrompt = event.value, isSaved = false, errorMessage = null)
            }

            is SettingsEvent.CustomSystemPromptEnabledChanged -> updateState {
                copy(
                    isCustomPromptEnabled = event.enabled,
                    isJsonFormatEnabled = if (event.enabled) false else isJsonFormatEnabled,
                    isSaved = false,
                    errorMessage = null
                )
            }

            is SettingsEvent.JsonFormatEnabledChanged -> updateState {
                copy(
                    isJsonFormatEnabled = event.enabled,
                    isCustomPromptEnabled = if (event.enabled) false else isCustomPromptEnabled,
                    isSaved = false,
                    errorMessage = null
                )
            }

            is SettingsEvent.TemperatureChanged -> updateState {
                copy(
                    temperature = event.value,
                    isSaved = false,
                    errorMessage = null
                )
            }

            SettingsEvent.Save -> saveSettings()
        }
    }

    private fun observeAssistantSettings() {
        observeAssistantJob?.cancel()
        observeAssistantJob = viewModelScope.launch {
            observeAssistantSettingsUseCase().collectLatest { settings ->
                updateState {
                    copy(
                        customSystemPrompt = settings.customSystemPrompt,
                        isCustomPromptEnabled = settings.isCustomPromptEnabled,
                        isJsonFormatEnabled = settings.isJsonFormatEnabled,
                        temperature = settings.temperature,
                        selectedModel = settings.model
                    )
                }
            }
        }
    }

    private fun observeCredentials() {
        observeCredentialsJob?.cancel()
        observeCredentialsJob = viewModelScope.launch {
            observeApiCredentialsUseCase().collectLatest { credentials ->
                updateState {
                    copy(apiKey = credentials?.apiKey.orEmpty())
                }
            }
        }
    }

    private fun saveSettings() {
        val currentState = _uiState.value
        if (currentState.isSaving) return
        updateState { copy(isSaving = true, isSaved = false, errorMessage = null) }
        viewModelScope.launch {
            runCatching {
                saveApiCredentialsUseCase(currentState.apiKey)
                saveAssistantSettingsUseCase(
                    useCustomSystemPrompt = currentState.isCustomPromptEnabled,
                    customSystemPrompt = currentState.customSystemPrompt,
                    useJsonFormat = currentState.isJsonFormatEnabled,
                    temperature = currentState.temperature,
                    model = currentState.selectedModel
                )
            }.onSuccess {
                updateState { copy(isSaving = false, isSaved = true, errorMessage = null) }
            }.onFailure { error ->
                updateState { copy(isSaving = false, errorMessage = error.message) }
            }
        }
    }

    private fun updateState(reducer: SettingsUiState.() -> SettingsUiState) {
        _uiState.value = _uiState.value.reducer()
    }
}
