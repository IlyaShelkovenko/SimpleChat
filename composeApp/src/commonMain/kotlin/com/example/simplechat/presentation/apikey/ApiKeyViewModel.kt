package com.example.simplechat.presentation.apikey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplechat.domain.usecase.SaveYandexCredentialsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ApiKeyViewModel(
    private val saveYandexCredentialsUseCase: SaveYandexCredentialsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ApiKeyUiState())
    val uiState: StateFlow<ApiKeyUiState> = _uiState.asStateFlow()

    private val _effects = Channel<ApiKeyEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: ApiKeyEvent) {
        when (event) {
            is ApiKeyEvent.ApiKeyChanged -> _uiState.value = _uiState.value.copy(
                apiKey = event.value,
                errorMessage = null
            )
            is ApiKeyEvent.FolderIdChanged -> _uiState.value = _uiState.value.copy(
                folderId = event.value,
                errorMessage = null
            )

            ApiKeyEvent.Submit -> submitApiKey()
        }
    }

    private fun submitApiKey() {
        val apiKey = _uiState.value.apiKey.trim()
        val folderId = _uiState.value.folderId.trim()
        if (apiKey.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid API key")
            emitError("Please enter a valid API key")
            return
        }
        if (folderId.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid folder ID")
            emitError("Please enter a valid folder ID")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null)
            runCatching {
                saveYandexCredentialsUseCase(apiKey, folderId)
            }.onSuccess {
                _uiState.value = _uiState.value.copy(isSubmitting = false)
                _effects.send(ApiKeyEffect.NavigateToChat)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isSubmitting = false, errorMessage = error.message)
                emitError(error.message ?: "Unable to save API key")
            }
        }
    }

    private fun emitError(message: String) {
        viewModelScope.launch {
            _effects.send(ApiKeyEffect.ShowError(message))
        }
    }
}
