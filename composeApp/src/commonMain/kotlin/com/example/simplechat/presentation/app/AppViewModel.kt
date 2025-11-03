package com.example.simplechat.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplechat.domain.usecase.ObserveApiKeyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(
    private val observeApiKeyUseCase: ObserveApiKeyUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AppState.Loading)
    val state: StateFlow<AppState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeApiKeyUseCase().collect { apiKey ->
                _state.value = if (apiKey.isNullOrBlank()) {
                    AppState.RequireApiKey
                } else {
                    AppState.Ready
                }
            }
        }
    }
}
