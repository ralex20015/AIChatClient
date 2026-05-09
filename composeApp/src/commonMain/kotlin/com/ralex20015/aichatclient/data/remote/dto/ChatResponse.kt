package com.ralex20015.aichatclient.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val model: String = "",
    @SerialName("created_at") val createdAt: String = "",
    val message: MessageDto? = null,
    val done: Boolean = false,
)
