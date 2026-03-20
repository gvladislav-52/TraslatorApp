package com.example.webinar_app.translate.data.local

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    fun create(): SqlDriver
}

//Объявляем такой класс, который будет реализован отдельно в Android/iOS (EXPECT тут, Actual на нужной платформе)