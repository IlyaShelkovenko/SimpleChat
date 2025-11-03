package com.example.simplechat.data.repository

import com.example.simplechat.data.network.ChatApiService
import com.example.simplechat.domain.model.ChatMessage
import com.example.simplechat.domain.model.MessageRole
import com.example.simplechat.domain.repository.ChatRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock

class ChatRepositoryImpl(
    private val apiService: ChatApiService
) : ChatRepository {
    override fun sendPrompt(
        apiKey: String,
        folderId: String,
        prompt: String
    ): Flow<Result<ChatMessage>> = flow {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val baseMessage = ChatMessage(
            id = "msg_$timestamp",
            role = MessageRole.ASSISTANT,
            content = "",
            createdAt = timestamp
        )

        try {
            apiService.streamPrompt(apiKey, folderId, prompt).collect { content ->
                emit(Result.success(baseMessage.copy(content = content)))
            }
        } catch (error: Throwable) {
            if (error is CancellationException) throw error

            val fallbackResult = runCatching {
                val response = apiService.sendPrompt(apiKey, folderId, prompt)
                val messageContent = response.result?.alternatives?.firstOrNull()?.message?.text
                    ?: throw IllegalStateException("Empty response from assistant")
                baseMessage.copy(content = messageContent.trim())
            }
            emit(fallbackResult)
        }
    }
}
