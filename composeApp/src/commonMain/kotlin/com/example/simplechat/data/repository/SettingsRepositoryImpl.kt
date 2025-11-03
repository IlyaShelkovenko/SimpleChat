package com.example.simplechat.data.repository

import com.example.simplechat.data.storage.SecureStorage
import com.example.simplechat.domain.model.YandexCredentials
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
private const val FOLDER_ID_STORAGE_KEY = "ai_folder_id"

class SettingsRepositoryImpl(
    private val secureStorage: SecureStorage,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : SettingsRepository {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val _credentialsState: MutableStateFlow<YandexCredentials?> = MutableStateFlow(null)

    init {
        scope.launch {
            val apiKey = secureStorage.read(API_KEY_STORAGE_KEY)
            val folderId = secureStorage.read(FOLDER_ID_STORAGE_KEY)
            _credentialsState.value = if (!apiKey.isNullOrBlank() && !folderId.isNullOrBlank()) {
                YandexCredentials(apiKey = apiKey, folderId = folderId)
            } else {
                null
            }
        }
    }

    override suspend fun saveCredentials(apiKey: String, folderId: String) {
        secureStorage.write(API_KEY_STORAGE_KEY, apiKey)
        secureStorage.write(FOLDER_ID_STORAGE_KEY, folderId)
        _credentialsState.value = YandexCredentials(apiKey = apiKey, folderId = folderId)
    }

    override suspend fun getCredentials(): YandexCredentials? {
        val current = _credentialsState.value
        if (current != null) return current

        val apiKey = secureStorage.read(API_KEY_STORAGE_KEY)
        val folderId = secureStorage.read(FOLDER_ID_STORAGE_KEY)

        return if (!apiKey.isNullOrBlank() && !folderId.isNullOrBlank()) {
            YandexCredentials(apiKey = apiKey, folderId = folderId).also {
                _credentialsState.value = it
            }
        } else {
            null
        }
    }

    override fun observeCredentials(): StateFlow<YandexCredentials?> = _credentialsState.asStateFlow()
}
