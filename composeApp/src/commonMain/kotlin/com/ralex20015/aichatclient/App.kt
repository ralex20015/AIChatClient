package com.ralex20015.aichatclient

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ralex20015.aichatclient.di.appModules
import com.ralex20015.aichatclient.presentation.screens.ChatListScreen
import com.ralex20015.aichatclient.presentation.screens.ChatScreen
import com.ralex20015.aichatclient.presentation.viewmodel.ChatListViewModel
import com.ralex20015.aichatclient.presentation.viewmodel.ChatViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf

sealed interface Screen {
    data object ConversationList : Screen
    data class Chat(val conversationId: String) : Screen
}

@Composable
fun App(platformModules: List<Module> = emptyList()) {
    KoinApplication(application = {
        modules(appModules + platformModules)
    }) {
        MaterialTheme {
            var screen by remember { mutableStateOf<Screen>(Screen.ConversationList) }

            when (val current = screen) {
                Screen.ConversationList -> {
                    val viewModel = koinViewModel<ChatListViewModel>()
                    val state by viewModel.state.collectAsState()

                    LaunchedEffect(viewModel) {
                        viewModel.openConversation.collect { id ->
                            screen = Screen.Chat(id)
                        }
                    }

                    ChatListScreen(
                        state = state,
                        onNewChat = viewModel::createConversation,
                        onOpenChat = { id -> screen = Screen.Chat(id) },
                        onDeleteChat = viewModel::deleteConversation,
                        onModelSelected = viewModel::selectModel,
                        onRetry = viewModel::retryLoadModels,
                    )
                }

                is Screen.Chat -> {
                    val viewModel = koinViewModel<ChatViewModel>(
                        parameters = { parametersOf(current.conversationId) },
                    )
                    val state by viewModel.state.collectAsState()

                    ChatScreen(
                        state = state,
                        onBack = { screen = Screen.ConversationList },
                        onInputChanged = viewModel::updateInput,
                        onSendMessage = viewModel::sendMessage,
                        onDismissError = viewModel::dismissError,
                    )
                }
            }
        }
    }
}
