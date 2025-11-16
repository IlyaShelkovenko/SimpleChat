package com.example.simplechat.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ConversationSummary(
    val id: String,
    val summary: String,
    val timestampMillis: Long
)
