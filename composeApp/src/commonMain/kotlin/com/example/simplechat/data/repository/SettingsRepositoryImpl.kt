package com.example.simplechat.data.repository

import com.example.simplechat.data.storage.SecureStorage
import com.example.simplechat.domain.model.ApiCredentials
import com.example.simplechat.domain.model.AssistantSettings
import com.example.simplechat.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val API_KEY_STORAGE_KEY = "ai_api_key"
private const val CUSTOM_PROMPT_STORAGE_KEY = "assistant_custom_prompt"
private const val CUSTOM_PROMPT_ENABLED_STORAGE_KEY = "assistant_custom_prompt_enabled"
private const val JSON_FORMAT_ENABLED_STORAGE_KEY = "assistant_json_format_enabled"
private const val TEMPERATURE_STORAGE_KEY = "assistant_temperature"
private const val MODEL_STORAGE_KEY = "assistant_model"
private const val DEFAULT_TEMPERATURE = 0.7

class SettingsRepositoryImpl(
    private val secureStorage: SecureStorage,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : SettingsRepository {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val _credentialsState: MutableStateFlow<ApiCredentials?> = MutableStateFlow(null)
    private val _assistantSettingsState: MutableStateFlow<AssistantSettings> =
        MutableStateFlow(AssistantSettings())

    init {
        scope.launch {
            val apiKey = secureStorage.read(API_KEY_STORAGE_KEY)
            _credentialsState.value = apiKey?.takeIf { it.isNotBlank() }?.let { key ->
                ApiCredentials(apiKey = key)
            }
        }
        scope.launch {
            val isEnabled = secureStorage.read(CUSTOM_PROMPT_ENABLED_STORAGE_KEY)?.toBooleanStrictOrNull() ?: false
            val prompt = secureStorage.read(CUSTOM_PROMPT_STORAGE_KEY).orEmpty()
            val isJsonFormatEnabled =
                secureStorage.read(JSON_FORMAT_ENABLED_STORAGE_KEY)?.toBooleanStrictOrNull() ?: false
            val temperature = secureStorage.read(TEMPERATURE_STORAGE_KEY)?.toDoubleOrNull() ?: DEFAULT_TEMPERATURE
            val model = secureStorage.read(MODEL_STORAGE_KEY) ?: AssistantSettings.DEFAULT_MODEL
            _assistantSettingsState.value = AssistantSettings(
                customSystemPrompt = prompt,
                isCustomPromptEnabled = isEnabled,
                isJsonFormatEnabled = isJsonFormatEnabled,
                temperature = temperature,
                model = model
            )
        }
    }

    override suspend fun saveApiKey(apiKey: String) {
        secureStorage.write(API_KEY_STORAGE_KEY, apiKey)
        _credentialsState.value = ApiCredentials(apiKey = apiKey)
    }

    override suspend fun getApiCredentials(): ApiCredentials? {
        val current = _credentialsState.value
        if (current != null) return current

        val apiKey = secureStorage.read(API_KEY_STORAGE_KEY)

        return apiKey?.takeIf { it.isNotBlank() }?.let { key ->
            ApiCredentials(apiKey = key).also { credentials ->
                _credentialsState.value = credentials
            }
        }
    }

    override fun observeApiCredentials(): StateFlow<ApiCredentials?> = _credentialsState.asStateFlow()

    override suspend fun saveAssistantSettings(
        useCustomSystemPrompt: Boolean,
        customSystemPrompt: String,
        useJsonFormat: Boolean,
        temperature: Double,
        model: String
    ) {
        secureStorage.write(CUSTOM_PROMPT_ENABLED_STORAGE_KEY, useCustomSystemPrompt.toString())
        secureStorage.write(CUSTOM_PROMPT_STORAGE_KEY, customSystemPrompt)
        secureStorage.write(JSON_FORMAT_ENABLED_STORAGE_KEY, useJsonFormat.toString())
        secureStorage.write(TEMPERATURE_STORAGE_KEY, temperature.toString())
        secureStorage.write(MODEL_STORAGE_KEY, model)
        _assistantSettingsState.value = AssistantSettings(
            customSystemPrompt = customSystemPrompt,
            isCustomPromptEnabled = useCustomSystemPrompt,
            isJsonFormatEnabled = useJsonFormat,
            temperature = temperature,
            model = model
        )
    }

    override suspend fun getAssistantSettings(): AssistantSettings {
        val current = _assistantSettingsState.value
        if (current != AssistantSettings()) return current

        val isEnabled = secureStorage.read(CUSTOM_PROMPT_ENABLED_STORAGE_KEY)?.toBooleanStrictOrNull() ?: false
        val prompt = secureStorage.read(CUSTOM_PROMPT_STORAGE_KEY).orEmpty()
        val isJsonFormatEnabled =
            secureStorage.read(JSON_FORMAT_ENABLED_STORAGE_KEY)?.toBooleanStrictOrNull() ?: false
        val temperature = secureStorage.read(TEMPERATURE_STORAGE_KEY)?.toDoubleOrNull() ?: DEFAULT_TEMPERATURE
        val model = secureStorage.read(MODEL_STORAGE_KEY) ?: AssistantSettings.DEFAULT_MODEL
        return AssistantSettings(
            customSystemPrompt = prompt,
            isCustomPromptEnabled = isEnabled,
            isJsonFormatEnabled = isJsonFormatEnabled,
            temperature = temperature,
            model = model
        ).also { settings ->
            _assistantSettingsState.value = settings
        }
    }

    override fun observeAssistantSettings(): StateFlow<AssistantSettings> = _assistantSettingsState.asStateFlow()
}
