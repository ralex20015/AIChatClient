package com.ralex20015.aichatclient.di

import com.ralex20015.aichatclient.data.local.DatabaseDriverFactory
import com.ralex20015.aichatclient.data.local.db.AIChatDatabase
import com.ralex20015.aichatclient.data.remote.OllamaApi
import com.ralex20015.aichatclient.data.repository.ConversationRepositoryImpl
import com.ralex20015.aichatclient.data.repository.OllamaRepositoryImpl
import com.ralex20015.aichatclient.domain.repository.ConversationRepository
import com.ralex20015.aichatclient.domain.repository.OllamaRepository
import com.ralex20015.aichatclient.presentation.viewmodel.ChatListViewModel
import com.ralex20015.aichatclient.presentation.viewmodel.ChatViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dataModule = module {
    single { OllamaApi() }
    single<OllamaRepository> { OllamaRepositoryImpl(get()) }
    single<ConversationRepository> {
        val driver = getOrNull<DatabaseDriverFactory>()?.createDriver()
        val database = driver?.let { AIChatDatabase(it) }
        ConversationRepositoryImpl(database)
    }
}

val viewModelModule = module {
    viewModelOf(::ChatListViewModel)
    viewModel { (conversationId: String) -> ChatViewModel(conversationId, get(), get()) }
}

val appModules = listOf(dataModule, viewModelModule)
