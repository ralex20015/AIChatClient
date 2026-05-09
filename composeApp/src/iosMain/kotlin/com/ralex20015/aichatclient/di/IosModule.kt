package com.ralex20015.aichatclient.di

import com.ralex20015.aichatclient.data.local.DatabaseDriverFactory
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory() }
}
