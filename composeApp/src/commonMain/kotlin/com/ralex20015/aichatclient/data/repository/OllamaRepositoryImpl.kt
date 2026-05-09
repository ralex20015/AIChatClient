package com.ralex20015.aichatclient.data.repository

import com.ralex20015.aichatclient.data.remote.OllamaApi
import com.ralex20015.aichatclient.domain.model.Message
import com.ralex20015.aichatclient.domain.model.OllamaModel
import com.ralex20015.aichatclient.domain.repository.OllamaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class OllamaRepositoryImpl(private val api: OllamaApi) : OllamaRepository {

    override suspend fun getAvailableModels(): List<OllamaModel> =
        api.getTags().models.map { OllamaModel(name = it.name, modifiedAt = it.modifiedAt, size = it.size) }

    override fun streamChat(model: String, messages: List<Message>): Flow<String> =
        api.streamChat(model, messages).transform { chunk ->
            chunk.message?.content?.let { if (it.isNotEmpty()) emit(it) }
        }
}
