package com.ralex20015.aichatclient

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.ralex20015.aichatclient.di.desktopModule

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "AI Chat",
    ) {
        App(platformModules = listOf(desktopModule))
    }
}
