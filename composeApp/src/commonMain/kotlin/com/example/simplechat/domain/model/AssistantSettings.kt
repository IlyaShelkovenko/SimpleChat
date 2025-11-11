package com.example.simplechat.domain.model

data class AssistantSettings(
    val customSystemPrompt: String = "",
    val isCustomPromptEnabled: Boolean = false,
    val isJsonFormatEnabled: Boolean = false,
    val temperature: Double = 0.7
)
