package com.example.webinar_app.translate.data.history

import com.example.webinar_app.translate.domain.history.HistoryItem
import database.HistoryEntity // этот файл сгенерирован SQLDelight

fun HistoryEntity.toHistoryItem(): HistoryItem {
    return HistoryItem(
        id = id,
        fromLanguageCode = fromLanguageCode,
        fromText = fromText,
        toLanguageCode = toLanguageCode,
        toText = toText
    )
}

// маппинг данных из базы в доменную модель.
// ПРЕОБРАЗОВАНИЕ из базы HistoryEntity в -> в объект бизнес-логики HistoryItem
