package com.ralex20015.aichatclient.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagsResponse(
    val models: List<ModelDto> = emptyList(),
)

@Serializable
data class ModelDto(
    val name: String,
    @SerialName("modified_at") val modifiedAt: String = "",
    val size: Long = 0L,
)
