package com.ralex20015.aichatclient.domain.model

data class Conversation(
    val id: String,
    val title: String,
    val model: String,
    val createdAt: Long,
    val updatedAt: Long,
)
