package com.example.simplechat.core.di

import com.example.simplechat.core.platform.PlatformConfiguration
import com.example.simplechat.core.platform.createSecureStorage
import com.example.simplechat.data.network.ChatCompletionService
import com.example.simplechat.data.network.HuggingFaceChatApiService
import com.example.simplechat.data.network.createHttpClient
import com.example.simplechat.data.repository.ChatRepositoryImpl
import com.example.simplechat.data.repository.SettingsRepositoryImpl
import com.example.simplechat.domain.repository.ChatRepository
import com.example.simplechat.domain.repository.SettingsRepository
import com.example.simplechat.domain.usecase.ObserveApiCredentialsUseCase
import com.example.simplechat.domain.usecase.ObserveAssistantSettingsUseCase
import com.example.simplechat.domain.usecase.SaveApiCredentialsUseCase
import com.example.simplechat.domain.usecase.SaveAssistantSettingsUseCase
import com.example.simplechat.domain.usecase.SendPromptUseCase

object AppGraph {
    private lateinit var configuration: PlatformConfiguration

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(createSecureStorage(configuration))
    }

    val chatService: ChatCompletionService by lazy {
        HuggingFaceChatApiService(createHttpClient())
    }

    val chatRepository: ChatRepository by lazy {
        ChatRepositoryImpl(
            apiService = chatService
        )
    }

    val saveApiCredentialsUseCase: SaveApiCredentialsUseCase by lazy { SaveApiCredentialsUseCase(settingsRepository) }
    val observeApiCredentialsUseCase: ObserveApiCredentialsUseCase by lazy { ObserveApiCredentialsUseCase(settingsRepository) }
    val observeAssistantSettingsUseCase: ObserveAssistantSettingsUseCase by lazy { ObserveAssistantSettingsUseCase(settingsRepository) }
    val saveAssistantSettingsUseCase: SaveAssistantSettingsUseCase by lazy { SaveAssistantSettingsUseCase(settingsRepository) }
    val sendPromptUseCase: SendPromptUseCase by lazy { SendPromptUseCase(chatRepository, settingsRepository) }

    fun initialize(platformConfiguration: PlatformConfiguration) {
        configuration = platformConfiguration
    }
}
