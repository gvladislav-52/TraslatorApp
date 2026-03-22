package com.example.webinar_app.translate.data.remote

import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

actual class HttpClientFactory {    //фабрика (сервис) для
    actual fun create(): HttpClient {   //создаем запрос на отправку запроса
        return HttpClient(Darwin) {
            install(ContentNegotiation) {
                json()
            }
        }
    }
}