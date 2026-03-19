package com.example.webinar_app.translate.data.translate

import com.example.webinar_app.core.domain.language.Language
import com.example.webinar_app.translate.domain.translate.TranslateClient
import com.example.webinar_app.translate.domain.translate.TranslateError
import com.example.webinar_app.translate.domain.translate.TranslateException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.errors.*

class KtorTranslateClient(
    private val httpClient: HttpClient
): TranslateClient {

    override suspend fun translate(
        fromLanguage: Language,
        fromText: String,
        toLanguage: Language
    ): String {
        val result = try {
            httpClient.post {
                url("https://api.langbly.com/language/translate/v2")
                contentType(ContentType.Application.Json)
                header("X-API-Key", "cbwVQ4LnuaFVDc7ZsHxxA")
                setBody(
                    TranslateDto(
                        textToTranslate = fromText,
                        sourceLanguageCode = fromLanguage.langCode,
                        targetLanguageCode = toLanguage.langCode
                    )
                )
            }
        } catch(e: IOException) {
            throw TranslateException(TranslateError.SERVICE_UNAVAILABLE)
        }
        val rawBody = result.body<String>()
        println("Raw response body: $rawBody")
        when(result.status.value) {
            in 200..299 -> Unit
            500 -> throw TranslateException(TranslateError.SERVER_ERROR)
            in 400..499 -> throw TranslateException(TranslateError.CLIENT_ERROR)
            else -> throw TranslateException(TranslateError.UNKNOWN_ERROR)
        }

        val responseDto = try {
            result.body<TranslatedResponse>()
        } catch(e: Exception) {
            println("Error parsing response: ${e.message}")
            throw TranslateException(TranslateError.SERVER_ERROR)
        }

// Берём первый перевод из списка
        val translatedText = responseDto.data.translations.firstOrNull()?.translatedText
            ?: throw TranslateException(TranslateError.SERVER_ERROR)

// Возвращаем текст перевода
        return translatedText
    }
}