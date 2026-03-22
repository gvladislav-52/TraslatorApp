package com.example.webinar_app.di

import com.example.webinar_app.database.TranslateDatabase
import com.example.webinar_app.translate.data.history.SqlDelightHistoryDataSource
import com.example.webinar_app.translate.data.local.DatabaseDriverFactory
import com.example.webinar_app.translate.data.remote.HttpClientFactory
import com.example.webinar_app.translate.data.translate.KtorTranslateClient
import com.example.webinar_app.translate.domain.history.HistoryDataSource
import com.example.webinar_app.translate.domain.translate.Translate
import com.example.webinar_app.translate.domain.translate.TranslateClient
import com.example.webinar_app.voice_to_text.domain.VoiceToTextParser

interface AppModule {   //интерфейс модуля
    val historyDataSource: HistoryDataSource    //история
    val client: TranslateClient     //данные для перевода
    val translateUseCase: Translate     //переведенные жданные
    val voiceParser: VoiceToTextParser  //парсер войса
}

class AppModuleImpl(
    parser: VoiceToTextParser       //класс
): AppModule {  //интерфейс

    override val historyDataSource: HistoryDataSource by lazy {
        SqlDelightHistoryDataSource(    //обращаемся к бд для создания данный в таблице
            TranslateDatabase(
                DatabaseDriverFactory().create()
            )
        )
    }

    override val client: TranslateClient by lazy {  //создаем запрос на отправку переводимого слова
        KtorTranslateClient(
            HttpClientFactory().create()
        )
    }

    override val translateUseCase: Translate by lazy {  //получаем данные с сетки и сохраняем в бд
        Translate(client, historyDataSource)
    }

    override val voiceParser = parser   //парсер для воспроизвдения музыки
}