package com.ralex20015.aichatclient.di

import android.content.Context
import com.ralex20015.aichatclient.data.local.DatabaseDriverFactory
import org.koin.dsl.module

fun androidModule(context: Context) = module {
    single { DatabaseDriverFactory(context) }
}
