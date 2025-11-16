package com.example.simplechat.presentation.history

import com.example.simplechat.domain.model.ConversationSummary

data class HistoryUiState(
    val summaries: List<ConversationSummary> = emptyList()
)
