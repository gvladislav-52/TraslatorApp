package com.example.webinar_app.translate.domain.translate

import com.example.webinar_app.core.domain.language.Language
import com.example.webinar_app.core.domain.util.Resource
import com.example.webinar_app.translate.domain.history.HistoryDataSource
import com.example.webinar_app.translate.domain.history.HistoryItem

class Translate(
    private val client: TranslateClient,    //параметр отвечает за сетевой запрос
    private val historyDataSource: HistoryDataSource    //параметр отвечает за локальное сохранение истории
) {

    suspend fun execute(    //метод suspend -> значит метод асинхронный (может останавливать выполнение, не блокируя поток)
        fromLanguage: Language,
        fromText: String,
        toLanguage: Language
    ): Resource<String> {   //Возвращает Resource<String> - обертку, которая может содержать результат или ошибку
        return try {
            val translatedText = client.translate(  //вызываем TranslateClient, получает переведенный текст
                fromLanguage, fromText, toLanguage
            )

            historyDataSource.insertHistoryItem(    //создаем обьект и сохраняем перевод в локальной бд
                HistoryItem(
                    id = null,
                    fromLanguageCode = fromLanguage.langCode,
                    fromText = fromText,
                    toLanguageCode = toLanguage.langCode,
                    toText = translatedText,
                )
            )

            Resource.Success(translatedText)    //если успешно, возвращаем переведенный текст
        } catch(e: TranslateException) {
            e.printStackTrace()
            Resource.Error(e)   //иначе оишбка
        }
    }
}