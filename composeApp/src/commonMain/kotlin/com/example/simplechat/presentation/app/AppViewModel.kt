package com.example.simplechat.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplechat.domain.usecase.ObserveYandexCredentialsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(
    private val observeYandexCredentialsUseCase: ObserveYandexCredentialsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AppState.Loading)
    val state: StateFlow<AppState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeYandexCredentialsUseCase().collect { credentials ->
                _state.value = if (credentials == null || credentials.apiKey.isBlank() || credentials.folderId.isBlank()) {
                    AppState.RequireCredentials
                } else {
                    AppState.Ready
                }
            }
        }
    }
}
