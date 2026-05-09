package com.ralex20015.aichatclient.domain.repository

import com.ralex20015.aichatclient.domain.model.Message
import com.ralex20015.aichatclient.domain.model.OllamaModel
import kotlinx.coroutines.flow.Flow

interface OllamaRepository {
    suspend fun getAvailableModels(): List<OllamaModel>
    fun streamChat(model: String, messages: List<Message>): Flow<String>
}
