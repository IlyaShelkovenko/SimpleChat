package com.example.simplechat.presentation.app

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.simplechat.core.di.AppGraph
import com.example.simplechat.presentation.apikey.ApiKeyViewModel
import com.example.simplechat.presentation.chat.ChatViewModel

object SimpleChatViewModelFactory {
    fun appViewModelFactory() = viewModelFactory {
        initializer {
            AppViewModel(AppGraph.observeYandexCredentialsUseCase)
        }
    }

    fun apiKeyViewModelFactory() = viewModelFactory {
        initializer {
            ApiKeyViewModel(AppGraph.saveYandexCredentialsUseCase)
        }
    }

    fun chatViewModelFactory() = viewModelFactory {
        initializer {
            ChatViewModel(AppGraph.sendPromptUseCase)
        }
    }
}
