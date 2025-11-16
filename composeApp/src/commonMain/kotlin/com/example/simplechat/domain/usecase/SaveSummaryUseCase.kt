package com.example.simplechat.domain.usecase

import com.example.simplechat.domain.repository.SummaryHistoryRepository

class SaveSummaryUseCase(
    private val repository: SummaryHistoryRepository
) {
    suspend operator fun invoke(summary: String) {
        val trimmed = summary.trim()
        if (trimmed.isEmpty()) return
        repository.saveSummary(trimmed)
    }
}
