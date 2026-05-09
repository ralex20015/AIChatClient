package com.ralex20015.aichatclient.data.local

import app.cash.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory {
    // SQLDelight persistence is not supported on the web target.
    // The app will use in-memory storage instead.
    actual fun createDriver(): SqlDriver? = null
}
