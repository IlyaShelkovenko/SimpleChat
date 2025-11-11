package com.example.simplechat.presentation.app

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.simplechat.core.di.AppGraph
import com.example.simplechat.presentation.apikey.ApiKeyViewModel
import com.example.simplechat.presentation.chat.ChatViewModel
import com.example.simplechat.presentation.settings.SettingsViewModel

object SimpleChatViewModelFactory {
    fun appViewModelFactory() = viewModelFactory {
        initializer {
            AppViewModel(AppGraph.observeApiCredentialsUseCase)
        }
    }

    fun apiKeyViewModelFactory() = viewModelFactory {
        initializer {
            ApiKeyViewModel(AppGraph.saveApiCredentialsUseCase)
        }
    }

    fun chatViewModelFactory() = viewModelFactory {
        initializer {
            ChatViewModel(AppGraph.sendPromptUseCase)
        }
    }

    fun settingsViewModelFactory() = viewModelFactory {
        initializer {
            SettingsViewModel(
                observeAssistantSettingsUseCase = AppGraph.observeAssistantSettingsUseCase,
                saveAssistantSettingsUseCase = AppGraph.saveAssistantSettingsUseCase,
                observeApiCredentialsUseCase = AppGraph.observeApiCredentialsUseCase,
                saveApiCredentialsUseCase = AppGraph.saveApiCredentialsUseCase
            )
        }
    }
}
