package com.ralex20015.aichatclient.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<MessageDto>,
    val stream: Boolean = true,
)

@Serializable
data class MessageDto(
    val role: String,
    val content: String,
)
