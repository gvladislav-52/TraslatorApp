package com.example.webinar_app.translate.domain.history

data class HistoryItem(
    val id: Long?,
    val fromLanguageCode: String,
    val fromText: String,
    val toLanguageCode: String,
    val toText: String,
)


//модель данных бизнес-логики