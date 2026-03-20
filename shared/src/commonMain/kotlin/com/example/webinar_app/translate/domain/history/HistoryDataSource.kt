package com.example.webinar_app.translate.domain.history

import com.example.webinar_app.core.domain.util.CommonFlow
import kotlin.coroutines.CoroutineContext


interface HistoryDataSource {   //интерфейс методы, которые нужно будет реализовать
    fun getHistory(context: CoroutineContext): CommonFlow<List<HistoryItem>>
    suspend fun insertHistoryItem(item: HistoryItem)
}