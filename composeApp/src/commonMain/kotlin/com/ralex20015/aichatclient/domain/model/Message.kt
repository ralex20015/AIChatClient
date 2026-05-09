package com.ralex20015.aichatclient.domain.model

data class Message(
    val id: String,
    val conversationId: String,
    val role: Role,
    val content: String,
    val createdAt: Long,
) {
    enum class Role { User, Assistant, System }
}
