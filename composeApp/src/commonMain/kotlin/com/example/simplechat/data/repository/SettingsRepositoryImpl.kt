package com.example.simplechat.data.repository

import com.example.simplechat.data.storage.SecureStorage
import com.example.simplechat.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.SupervisorJob

private const val API_KEY_STORAGE_KEY = "ai_api_key"

class SettingsRepositoryImpl(
    private val secureStorage: SecureStorage,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : SettingsRepository {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val _apiKeyState: MutableStateFlow<String?> = MutableStateFlow(null)

    init {
        scope.launch {
            _apiKeyState.value = secureStorage.read(API_KEY_STORAGE_KEY)
        }
    }

    override suspend fun saveApiKey(apiKey: String) {
        secureStorage.write(API_KEY_STORAGE_KEY, apiKey)
        _apiKeyState.value = apiKey
    }

    override suspend fun getApiKey(): String? {
        val current = _apiKeyState.value
        return current ?: secureStorage.read(API_KEY_STORAGE_KEY).also {
            _apiKeyState.value = it
        }
    }

    override fun observeApiKey(): StateFlow<String?> = _apiKeyState.asStateFlow()
}
