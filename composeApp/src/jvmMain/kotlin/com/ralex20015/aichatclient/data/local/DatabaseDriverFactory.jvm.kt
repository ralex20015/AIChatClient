package com.ralex20015.aichatclient.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.ralex20015.aichatclient.data.local.db.AIChatDatabase
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val dbDir = File(System.getProperty("user.home"), ".aichatclient")
        dbDir.mkdirs()
        val dbFile = File(dbDir, "aichat.db")
        val isNew = !dbFile.exists()
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        if (isNew) AIChatDatabase.Schema.create(driver)
        return driver
    }
}
