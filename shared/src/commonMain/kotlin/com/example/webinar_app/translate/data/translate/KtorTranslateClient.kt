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
    private val httpClient: HttpClient  //конструктор
): TranslateClient {    //протокол

    override suspend fun translate(     //реализовываем метод прокотола
        fromLanguage: Language,
        fromText: String,
        toLanguage: Language
    ): String {                         // возвращаемый тип String
        val result = try {  //создаем пост запрос
            httpClient.post {
                url("https://api.langbly.com/language/translate/v2")
                contentType(ContentType.Application.Json)
                header("X-API-Key", "cbwVQ4LnuaFVDc7ZsHxxA")    //хедер с апи ключом для обращенич
                setBody(                        //установка боди теля для запроса (отправка нашей дто модели)
                    TranslateDto(
                        textToTranslate = fromText,
                        sourceLanguageCode = fromLanguage.langCode,
                        targetLanguageCode = toLanguage.langCode
                    )
                )
            }
        } catch(e: IOException) {   //исключение выкидываем ошибки ввода
            throw TranslateException(TranslateError.SERVICE_UNAVAILABLE)
        }
        val rawBody = result.body<String>() // сырые данные
        println("Raw response body: $rawBody")
        when(result.status.value) {
            in 200..299 -> Unit //значит все ок, идем дальше
            500 -> throw TranslateException(TranslateError.SERVER_ERROR)
            in 400..499 -> throw TranslateException(TranslateError.CLIENT_ERROR)
            else -> throw TranslateException(TranslateError.UNKNOWN_ERROR)
        }

        val responseDto = try {
            result.body<TranslatedResponse>() //парсим данные JSON в Kotlin объекты TranslatedResponse
        } catch(e: Exception) {
            println("Error parsing response: ${e.message}")
            throw TranslateException(TranslateError.SERVER_ERROR)
        }

// Берём первый перевод из списка
        val translatedText = responseDto.data.translations.firstOrNull()?.translatedText    //берем первый перевод из списка, тк как может вернуть несколько
            ?: throw TranslateException(TranslateError.SERVER_ERROR)

// Возвращаем текст перевода
        return translatedText   //возвращаем его
    }
}