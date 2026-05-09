package com.ralex20015.aichatclient.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.ralex20015.aichatclient.data.local.db.AIChatDatabase
import com.ralex20015.aichatclient.data.local.db.Conversation as ConversationEntity
import com.ralex20015.aichatclient.data.local.db.Message as MessageEntity
import com.ralex20015.aichatclient.domain.model.Conversation
import com.ralex20015.aichatclient.domain.model.Message
import com.ralex20015.aichatclient.domain.repository.ConversationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import com.ralex20015.aichatclient.util.currentTimeMillis
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ConversationRepositoryImpl(
    private val database: AIChatDatabase?,
) : ConversationRepository {

    // In-memory fallback when no database driver is available (web targets)
    private val inMemoryConversations = MutableStateFlow<List<Conversation>>(emptyList())
    private val inMemoryMessages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())

    override fun getAllConversations(): Flow<List<Conversation>> {
        return database?.conversationQueries
            ?.getAllConversations()
            ?.asFlow()
            ?.mapToList(Dispatchers.Default)
            ?.map { list -> list.map { it.toDomain() } }
            ?: inMemoryConversations
    }

    override suspend fun getConversationById(id: String): Conversation? {
        return database?.conversationQueries
            ?.getConversationById(id)
            ?.executeAsOneOrNull()
            ?.toDomain()
            ?: inMemoryConversations.value.find { it.id == id }
    }

    override suspend fun createConversation(title: String, model: String): Conversation {
        val now = currentTimeMillis()
        val conversation = Conversation(
            id = Uuid.random().toString(),
            title = title,
            model = model,
            createdAt = now,
            updatedAt = now,
        )
        database?.conversationQueries?.insertConversation(
            id = conversation.id,
            title = conversation.title,
            model = conversation.model,
            createdAt = conversation.createdAt,
            updatedAt = conversation.updatedAt,
        ) ?: inMemoryConversations.update { it + conversation }
        return conversation
    }

    override suspend fun updateConversationTitle(id: String, title: String) {
        val now = currentTimeMillis()
        database?.conversationQueries?.updateConversation(title = title, updatedAt = now, id = id)
            ?: inMemoryConversations.update { list ->
                list.map { if (it.id == id) it.copy(title = title, updatedAt = now) else it }
            }
    }

    override suspend fun deleteConversation(id: String) {
        database?.let { db ->
            db.messageQueries.deleteMessagesForConversation(id)
            db.conversationQueries.deleteConversation(id)
        } ?: run {
            inMemoryMessages.update { it - id }
            inMemoryConversations.update { list -> list.filter { it.id != id } }
        }
    }

    override fun getMessagesForConversation(conversationId: String): Flow<List<Message>> {
        return database?.messageQueries
            ?.getMessagesForConversation(conversationId)
            ?.asFlow()
            ?.mapToList(Dispatchers.Default)
            ?.map { list -> list.map { it.toDomain() } }
            ?: inMemoryMessages.map { it[conversationId] ?: emptyList() }
    }

    override suspend fun addMessage(
        conversationId: String,
        role: Message.Role,
        content: String,
    ): Message {
        val now = currentTimeMillis()
        val message = Message(
            id = Uuid.random().toString(),
            conversationId = conversationId,
            role = role,
            content = content,
            createdAt = now,
        )
        database?.messageQueries?.insertMessage(
            id = message.id,
            conversationId = message.conversationId,
            role = message.role.name.lowercase(),
            content = message.content,
            createdAt = message.createdAt,
        ) ?: inMemoryMessages.update { map ->
            val current = map[conversationId] ?: emptyList()
            map + (conversationId to current + message)
        }
        return message
    }

    private fun ConversationEntity.toDomain() = Conversation(
        id = id,
        title = title,
        model = model,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    private fun MessageEntity.toDomain() = Message(
        id = id,
        conversationId = conversationId,
        role = when (role) {
            "assistant" -> Message.Role.Assistant
            "system" -> Message.Role.System
            else -> Message.Role.User
        },
        content = content,
        createdAt = createdAt,
    )
}
