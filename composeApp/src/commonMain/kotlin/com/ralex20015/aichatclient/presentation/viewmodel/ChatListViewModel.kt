package com.ralex20015.aichatclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralex20015.aichatclient.domain.model.Conversation
import com.ralex20015.aichatclient.domain.model.OllamaModel
import com.ralex20015.aichatclient.domain.repository.ConversationRepository
import com.ralex20015.aichatclient.domain.repository.OllamaRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatListState(
    val conversations: List<Conversation> = emptyList(),
    val availableModels: List<OllamaModel> = emptyList(),
    val selectedModel: String = "",
    val isLoadingModels: Boolean = false,
    val error: String? = null,
)

class ChatListViewModel(
    private val conversationRepository: ConversationRepository,
    private val ollamaRepository: OllamaRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatListState())
    val state: StateFlow<ChatListState> = _state.asStateFlow()

    // Emits the ID of a newly created conversation to trigger navigation
    private val _openConversation = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val openConversation: SharedFlow<String> = _openConversation.asSharedFlow()

    init {
        observeConversations()
        loadModels()
    }

    private fun observeConversations() {
        viewModelScope.launch {
            conversationRepository.getAllConversations().collect { conversations ->
                _state.update { it.copy(conversations = conversations) }
            }
        }
    }

    private fun loadModels() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingModels = true, error = null) }
            try {
                val models = ollamaRepository.getAvailableModels()
                _state.update {
                    it.copy(
                        availableModels = models,
                        selectedModel = models.firstOrNull()?.name ?: it.selectedModel,
                        isLoadingModels = false,
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingModels = false,
                        error = "Cannot connect to Ollama. Is it running?",
                    )
                }
            }
        }
    }

    fun selectModel(model: String) {
        _state.update { it.copy(selectedModel = model) }
    }

    fun createConversation() {
        val model = _state.value.selectedModel.ifBlank { return }
        viewModelScope.launch {
            val conversation = conversationRepository.createConversation(
                title = "New chat",
                model = model,
            )
            _openConversation.emit(conversation.id)
        }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch { conversationRepository.deleteConversation(id) }
    }

    fun retryLoadModels() = loadModels()
}
