package com.ralex20015.aichatclient.domain.repository

import com.ralex20015.aichatclient.domain.model.Conversation
import com.ralex20015.aichatclient.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun getAllConversations(): Flow<List<Conversation>>
    suspend fun getConversationById(id: String): Conversation?
    suspend fun createConversation(title: String, model: String): Conversation
    suspend fun updateConversationTitle(id: String, title: String)
    suspend fun deleteConversation(id: String)
    fun getMessagesForConversation(conversationId: String): Flow<List<Message>>
    suspend fun addMessage(conversationId: String, role: Message.Role, content: String): Message
}
