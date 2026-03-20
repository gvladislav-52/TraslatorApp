package com.example.webinar_app.translate.data.history

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.webinar_app.core.domain.util.CommonFlow
import com.example.webinar_app.core.domain.util.toCommonFlow
import com.example.webinar_app.database.TranslateDatabase
import com.example.webinar_app.translate.domain.history.HistoryDataSource
import com.example.webinar_app.translate.domain.history.HistoryItem
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlin.coroutines.CoroutineContext

class SqlDelightHistoryDataSource(  //Конструктор, куда передается база данных
    db: TranslateDatabase
): HistoryDataSource {  //интерфейс с методами (реализация getHistory, insertHistoryItem)

    private val queries = db.translateQueries // получаем доступ к SQL-запросм (getHistory, inserHistoryEntity)

    override fun getHistory(context: CoroutineContext): CommonFlow<List<HistoryItem>> {
        return queries // метод возвращает список историй переводов в виде Flow
            .getHistory() //вызываем SQL запрос
            .asFlow() // превращаем запрос в Flow
            .mapToList(context) //превращает результат в список <List<HistoryItem>
            .map { history ->
                history.map { it.toHistoryItem() }
                //маппинг HistoryEntity->HistoryItem
            }
            .toCommonFlow()
                // превращает в CommonFlow
    }

    override suspend fun insertHistoryItem(item: HistoryItem) {
        queries.insertHistoryEntity(    //функция добавляет новый перевод в базу
            id = item.id,   //HistoryItem передаем запись на тип бд для сохранения
            fromLanguageCode = item.fromLanguageCode,
            fromText = item.fromText,
            toLanguageCode = item.toLanguageCode,
            toText = item.toText,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
    }
}

//Это реализация источника данных для истории переводов
// файл является мостом между бизнес логикой domain и базой данных sqldelight