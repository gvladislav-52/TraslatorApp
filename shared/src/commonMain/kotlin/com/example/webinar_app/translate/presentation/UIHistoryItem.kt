package com.example.webinar_app.translate.presentation

import com.example.webinar_app.core.presentation.UiLanguage

data class UiHistoryItem(
    val id: Long,
    val fromText: String,
    val toText: String,
    val fromLanguage: UiLanguage,
    val toLanguage: UiLanguage
)

// Хранит данные для отображения истории переводов на экране
// - это объект который UI понимает и отображает, а не база данных и не сетевой клиент

// HistoryEntity - модель, базы данных
// HistoryItem - доменная модель, отражает данные бизнес лоигки
// UiHistoryItem - представление для UI