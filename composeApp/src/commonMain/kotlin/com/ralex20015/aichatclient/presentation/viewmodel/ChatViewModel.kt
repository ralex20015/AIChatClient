package com.ralex20015.aichatclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralex20015.aichatclient.domain.model.Conversation
import com.ralex20015.aichatclient.domain.model.Message
import com.ralex20015.aichatclient.domain.repository.ConversationRepository
import com.ralex20015.aichatclient.domain.repository.OllamaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatState(
    val conversation: Conversation? = null,
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val isStreaming: Boolean = false,
    val streamingContent: String = "",
    val error: String? = null,
)

class ChatViewModel(
    private val conversationId: String,
    private val conversationRepository: ConversationRepository,
    private val ollamaRepository: OllamaRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val conversation = conversationRepository.getConversationById(conversationId)
            _state.update { it.copy(conversation = conversation) }
        }
        viewModelScope.launch {
            conversationRepository.getMessagesForConversation(conversationId).collect { messages ->
                _state.update { it.copy(messages = messages) }
            }
        }
    }

    fun updateInput(text: String) {
        _state.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val text = _state.value.inputText.trim()
        if (text.isBlank() || _state.value.isStreaming) return
        val model = _state.value.conversation?.model ?: return

        viewModelScope.launch {
            val previousMessages = _state.value.messages
            _state.update { it.copy(inputText = "", isStreaming = true, streamingContent = "", error = null) }

            conversationRepository.addMessage(conversationId, Message.Role.User, text)

            // Set title from first user message
            if (previousMessages.none { it.role == Message.Role.User }) {
                conversationRepository.updateConversationTitle(conversationId, text.take(50))
            }

            val apiMessages = previousMessages + Message(
                id = "",
                conversationId = conversationId,
                role = Message.Role.User,
                content = text,
                createdAt = 0,
            )

            var fullResponse = ""
            try {
                ollamaRepository.streamChat(model, apiMessages).collect { chunk ->
                    fullResponse += chunk
                    _state.update { it.copy(streamingContent = fullResponse) }
                }
                conversationRepository.addMessage(conversationId, Message.Role.Assistant, fullResponse)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error: ${e.message}") }
            } finally {
                _state.update { it.copy(isStreaming = false, streamingContent = "") }
            }
        }
    }

    fun dismissError() {
        _state.update { it.copy(error = null) }
    }
}
