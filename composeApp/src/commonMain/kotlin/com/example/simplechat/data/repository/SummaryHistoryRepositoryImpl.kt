package com.example.simplechat.data.repository

import com.example.simplechat.data.storage.SecureStorage
import com.example.simplechat.domain.model.ConversationSummary
import com.example.simplechat.domain.repository.SummaryHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.random.Random

class SummaryHistoryRepositoryImpl(
    private val secureStorage: SecureStorage,
    private val json: Json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }
) : SummaryHistoryRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val historyFlow = MutableStateFlow<List<ConversationSummary>>(emptyList())

    init {
        scope.launch {
            historyFlow.value = readHistory()
        }
    }

    override suspend fun saveSummary(summary: String) {
        val trimmed = summary.trim()
        if (trimmed.isEmpty()) return
        val entry = ConversationSummary(
            id = generateId(),
            summary = trimmed,
            timestampMillis = Clock.System.now().toEpochMilliseconds()
        )
        val updated = listOf(entry) + historyFlow.value
        historyFlow.value = updated
        persist(updated)
    }

    override fun observeSummaryHistory() = historyFlow.asStateFlow()

    override suspend fun clearHistory() {
        historyFlow.value = emptyList()
        secureStorage.remove(HISTORY_KEY)
    }

    private suspend fun persist(history: List<ConversationSummary>) {
        secureStorage.write(HISTORY_KEY, json.encodeToString(history))
    }

    private suspend fun readHistory(): List<ConversationSummary> {
        val stored = secureStorage.read(HISTORY_KEY) ?: return emptyList()
        return runCatching {
            json.decodeFromString<List<ConversationSummary>>(stored)
        }.getOrDefault(emptyList())
    }

    private fun generateId(): String {
        val time = Clock.System.now().toEpochMilliseconds()
        val random = Random.nextInt(0, Int.MAX_VALUE)
        return "${time}_$random"
    }

    companion object {
        private const val HISTORY_KEY = "conversation_history"
    }
}
