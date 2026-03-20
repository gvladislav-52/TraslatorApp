package com.example.webinar_app.translate.data.remote

import io.ktor.client.*

expect class HttpClientFactory {
    fun create(): HttpClient
}

//Обявляем класс для платформ, что нужен HTTP клиент, но каждая платформа реализует его по своему.
