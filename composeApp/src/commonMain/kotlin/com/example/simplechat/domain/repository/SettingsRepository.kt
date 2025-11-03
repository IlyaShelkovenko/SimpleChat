package com.example.simplechat.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun saveApiKey(apiKey: String)
    suspend fun getApiKey(): String?
    fun observeApiKey(): Flow<String?>
}
