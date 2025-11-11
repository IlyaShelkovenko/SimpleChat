package com.example.simplechat.domain.model

data class AssistantSettings(
    val customSystemPrompt: String = "",
    val isCustomPromptEnabled: Boolean = false,
    val isJsonFormatEnabled: Boolean = false,
    val temperature: Double = 0.7,
    val model: String = DEFAULT_MODEL
) {
    companion object {
        const val DEFAULT_MODEL: String = "deepseek-ai/DeepSeek-R1"
    }
}
