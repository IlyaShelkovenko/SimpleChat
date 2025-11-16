package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.repository.SummaryHistoryRepository

class ClearSummaryHistoryUseCase(
    private val repository: SummaryHistoryRepository
) {
    suspend operator fun invoke() = repository.clearHistory()
}
