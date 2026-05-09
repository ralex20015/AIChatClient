package com.ralex20015.aichatclient.data.remote

import com.ralex20015.aichatclient.data.remote.dto.ChatRequest
import com.ralex20015.aichatclient.data.remote.dto.ChatResponse
import com.ralex20015.aichatclient.data.remote.dto.MessageDto
import com.ralex20015.aichatclient.data.remote.dto.TagsResponse
import com.ralex20015.aichatclient.domain.model.Message
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class OllamaApi(val baseUrl: String = "http://localhost:11434") {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient {
        install(ContentNegotiation) { json(json) }
        install(Logging) { level = LogLevel.NONE }
        install(HttpTimeout) {
            connectTimeoutMillis = 5_000
            requestTimeoutMillis = Long.MAX_VALUE
            socketTimeoutMillis = Long.MAX_VALUE
        }
    }

    suspend fun getTags(): TagsResponse = client.get("$baseUrl/api/tags").body()

    fun streamChat(model: String, messages: List<Message>): Flow<ChatResponse> = flow {
        val request = ChatRequest(
            model = model,
            messages = messages.map {
                MessageDto(role = it.role.name.lowercase(), content = it.content)
            },
        )
        client.preparePost("$baseUrl/api/chat") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.execute { response ->
            val channel = response.body<io.ktor.utils.io.ByteReadChannel>()
            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break
                if (line.isNotBlank()) {
                    val chunk = json.decodeFromString<ChatResponse>(line)
                    emit(chunk)
                    if (chunk.done) return@execute
                }
            }
        }
    }
}
