package com.example.webinar_app.translate.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.webinar_app.database.TranslateDatabase

actual class DatabaseDriverFactory {    //фабрика для обращения к бд
    actual fun create(): SqlDriver {
        return NativeSqliteDriver(TranslateDatabase.Schema,  "translate.db")
    //название схемы и ее имя для сохранения
    }
}