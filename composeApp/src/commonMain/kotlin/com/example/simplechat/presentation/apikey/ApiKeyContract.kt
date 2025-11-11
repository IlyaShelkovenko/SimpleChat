package com.example.simplechat.presentation.apikey

data class ApiKeyUiState(
    val apiKey: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

sealed interface ApiKeyEvent {
    data class ApiKeyChanged(val value: String) : ApiKeyEvent
    data object Submit : ApiKeyEvent
}

sealed interface ApiKeyEffect {
    data object NavigateToChat : ApiKeyEffect
    data class ShowError(val message: String) : ApiKeyEffect
}
