package com.example.simplechat.domain.repository

import com.example.simplechat.domain.model.ConversationSummary
import kotlinx.coroutines.flow.Flow

interface SummaryHistoryRepository {
    suspend fun saveSummary(summary: String)
    fun observeSummaryHistory(): Flow<List<ConversationSummary>>
    suspend fun clearHistory()
}
