package com.ralex20015.aichatclient.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.ralex20015.aichatclient.data.local.db.AIChatDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver = AndroidSqliteDriver(
        schema = AIChatDatabase.Schema,
        context = context,
        name = "aichat.db",
    )
}
