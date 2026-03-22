package com.example.webinar_app.translate.data.remote

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

actual class HttpClientFactory {
    actual fun create(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json()
            }
        }
    }
}

//сервис для работы с сетевыми запросами на андоиде (1 в 1 как ios)