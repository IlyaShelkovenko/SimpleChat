package com.example.simplechat.core.di

import com.example.simplechat.core.platform.PlatformConfiguration
import com.example.simplechat.core.platform.createSecureStorage
import com.example.simplechat.data.network.ChatApiService
import com.example.simplechat.data.network.createHttpClient
import com.example.simplechat.data.repository.ChatRepositoryImpl
import com.example.simplechat.data.repository.SettingsRepositoryImpl
import com.example.simplechat.domain.repository.ChatRepository
import com.example.simplechat.domain.repository.SettingsRepository
import com.example.simplechat.domain.usecase.GetYandexCredentialsUseCase
import com.example.simplechat.domain.usecase.ObserveYandexCredentialsUseCase
import com.example.simplechat.domain.usecase.ObserveAssistantSettingsUseCase
import com.example.simplechat.domain.usecase.SaveYandexCredentialsUseCase
import com.example.simplechat.domain.usecase.SaveAssistantSettingsUseCase
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

    val getYandexCredentialsUseCase: GetYandexCredentialsUseCase by lazy { GetYandexCredentialsUseCase(settingsRepository) }
    val saveYandexCredentialsUseCase: SaveYandexCredentialsUseCase by lazy { SaveYandexCredentialsUseCase(settingsRepository) }
    val observeYandexCredentialsUseCase: ObserveYandexCredentialsUseCase by lazy { ObserveYandexCredentialsUseCase(settingsRepository) }
    val observeAssistantSettingsUseCase: ObserveAssistantSettingsUseCase by lazy { ObserveAssistantSettingsUseCase(settingsRepository) }
    val saveAssistantSettingsUseCase: SaveAssistantSettingsUseCase by lazy { SaveAssistantSettingsUseCase(settingsRepository) }
    val sendPromptUseCase: SendPromptUseCase by lazy { SendPromptUseCase(chatRepository, settingsRepository) }

    fun initialize(platformConfiguration: PlatformConfiguration) {
        configuration = platformConfiguration
    }
}
