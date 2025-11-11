package com.example.simplechat.domain.repository

import com.example.simplechat.domain.model.ApiCredentials
import com.example.simplechat.domain.model.AssistantSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun saveApiKey(apiKey: String)
    suspend fun getApiCredentials(): ApiCredentials?
    fun observeApiCredentials(): Flow<ApiCredentials?>
    suspend fun saveAssistantSettings(
        useCustomSystemPrompt: Boolean,
        customSystemPrompt: String,
        useJsonFormat: Boolean,
        temperature: Double,
        model: String
    )
    suspend fun getAssistantSettings(): AssistantSettings
    fun observeAssistantSettings(): Flow<AssistantSettings>
}
