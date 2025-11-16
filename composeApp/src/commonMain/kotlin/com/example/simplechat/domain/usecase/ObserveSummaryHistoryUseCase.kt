package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.model.ConversationSummary
import com.example.simplechat.domain.repository.SummaryHistoryRepository
import kotlinx.coroutines.flow.Flow

class ObserveSummaryHistoryUseCase(
    private val repository: SummaryHistoryRepository
) {
    operator fun invoke(): Flow<List<ConversationSummary>> = repository.observeSummaryHistory()
}
