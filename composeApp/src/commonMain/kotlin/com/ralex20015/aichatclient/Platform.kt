package com.ralex20015.aichatclient

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform