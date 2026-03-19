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

interface AppModule {
    val historyDataSource: HistoryDataSource
    val client: TranslateClient
    val translateUseCase: Translate
    val voiceParser: VoiceToTextParser
}

class AppModuleImpl(
    parser: VoiceToTextParser
): AppModule {

    override val historyDataSource: HistoryDataSource by lazy {
        SqlDelightHistoryDataSource(
            TranslateDatabase(
                DatabaseDriverFactory().create()
            )
        )
    }

    override val client: TranslateClient by lazy {
        KtorTranslateClient(
            HttpClientFactory().create()
        )
    }

    override val translateUseCase: Translate by lazy {
        Translate(client, historyDataSource)
    }

    override val voiceParser = parser
}