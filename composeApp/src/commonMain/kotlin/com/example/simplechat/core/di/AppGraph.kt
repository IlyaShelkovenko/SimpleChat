package com.example.simplechat.core.di

import com.example.simplechat.core.platform.PlatformConfiguration
import com.example.simplechat.core.platform.createSecureStorage
import com.example.simplechat.data.network.ChatApiService
import com.example.simplechat.data.network.createHttpClient
import com.example.simplechat.data.repository.ChatRepositoryImpl
import com.example.simplechat.data.repository.SettingsRepositoryImpl
import com.example.simplechat.domain.repository.ChatRepository
import com.example.simplechat.domain.repository.SettingsRepository
import com.example.simplechat.domain.usecase.GetApiKeyUseCase
import com.example.simplechat.domain.usecase.ObserveApiKeyUseCase
import com.example.simplechat.domain.usecase.SaveApiKeyUseCase
import com.example.simplechat.domain.usecase.SendPromptUseCase

object AppGraph {
    private lateinit var configuration: PlatformConfiguration

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(createSecureStorage(configuration))
    }

    val chatRepository: ChatRepository by lazy {
        ChatRepositoryImpl(
            apiService = ChatApiService(createHttpClient())
        )
    }

    val getApiKeyUseCase: GetApiKeyUseCase by lazy { GetApiKeyUseCase(settingsRepository) }
    val saveApiKeyUseCase: SaveApiKeyUseCase by lazy { SaveApiKeyUseCase(settingsRepository) }
    val observeApiKeyUseCase: ObserveApiKeyUseCase by lazy { ObserveApiKeyUseCase(settingsRepository) }
    val sendPromptUseCase: SendPromptUseCase by lazy { SendPromptUseCase(chatRepository, settingsRepository) }

    fun initialize(platformConfiguration: PlatformConfiguration) {
        configuration = platformConfiguration
    }
}
