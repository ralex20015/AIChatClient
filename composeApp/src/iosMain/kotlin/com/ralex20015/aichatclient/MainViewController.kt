package com.ralex20015.aichatclient

import androidx.compose.ui.window.ComposeUIViewController
import com.ralex20015.aichatclient.di.iosModule

fun MainViewController() = ComposeUIViewController {
    App(platformModules = listOf(iosModule))
}
