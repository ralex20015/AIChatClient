package com.ralex20015.aichatclient.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.ralex20015.aichatclient.data.local.db.AIChatDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver = NativeSqliteDriver(
        schema = AIChatDatabase.Schema,
        name = "aichat.db",
    )
}
