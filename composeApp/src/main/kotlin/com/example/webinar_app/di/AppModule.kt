package com.example.webinar_app.di

import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import com.example.webinar_app.database.TranslateDatabase
import com.example.webinar_app.translate.data.history.SqlDelightHistoryDataSource
import com.example.webinar_app.translate.data.local.DatabaseDriverFactory
import com.example.webinar_app.translate.data.remote.HttpClientFactory
import com.example.webinar_app.translate.data.translate.KtorTranslateClient
import com.example.webinar_app.translate.domain.history.HistoryDataSource
import com.example.webinar_app.translate.domain.translate.Translate
import com.example.webinar_app.translate.domain.translate.TranslateClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

//это центральное место, где мы говорим Hilt, как создавать: нужные там компоненты (то это DI контейнер)
@Module //говорит Hilt, что это модуль зависимостей
@InstallIn(SingletonComponent::class)   //бъекты создаются один раз на весь lifecycle приложения (singleton)
object AppModule { //синглтон, который содержит функции-провайдеры
    //: Hilt будет знать, как создавать HttpClient, TranslateClient, базу данных и т.д.
    @Provides //эта функция создаёт зависимость, которую Hilt может инжектить
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClientFactory().create() //Возвращает HttpClient (Ktor), который используется для перевода
    }

    @Provides
    @Singleton
    fun provideTranslateClient(httpClient: HttpClient): TranslateClient {
        return KtorTranslateClient(httpClient) //Возвращаем клиент перевода, который использует Ktor
    }

    @Provides
    @Singleton
    fun provideDatabaseDriver(app: Application): SqlDriver {
        return DatabaseDriverFactory(app).create() //Создаём драйвер для SQLDelight (локальная база)
    }

    @Provides
    @Singleton
    fun provideHistoryDataSource(driver: SqlDriver): HistoryDataSource {
        return SqlDelightHistoryDataSource(TranslateDatabase(driver)) //История переводов хранится в базе
    }

    @Provides
    @Singleton
    fun provideTranslateUseCase(
        client: TranslateClient,
        dataSource: HistoryDataSource
    ): Translate {
        return Translate(client, dataSource)    //Основной кейс: делает перевод и сохраняет историю
    }
}