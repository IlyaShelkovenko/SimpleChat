package com.example.simplechat.domain.repository

import com.example.simplechat.domain.model.AssistantSettings
import com.example.simplechat.domain.model.YandexCredentials
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun saveCredentials(apiKey: String, folderId: String)
    suspend fun getCredentials(): YandexCredentials?
    fun observeCredentials(): Flow<YandexCredentials?>
    suspend fun saveAssistantSettings(useCustomSystemPrompt: Boolean, customSystemPrompt: String)
    suspend fun getAssistantSettings(): AssistantSettings
    fun observeAssistantSettings(): Flow<AssistantSettings>
}
