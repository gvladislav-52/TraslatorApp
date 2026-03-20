package com.example.webinar_app.translate.presentation

import com.example.webinar_app.core.presentation.UiLanguage
import com.example.webinar_app.translate.domain.translate.TranslateError

data class TranslateState(  //контейнер состояния, все что нужно ui, чтоб отобразить экран, хранится здесь
    val fromText: String = "",
    val toText: String? = null,
    val isTranslating: Boolean = false,
    val fromLanguage: UiLanguage = UiLanguage.byCode("en"),
    val toLanguage: UiLanguage = UiLanguage.byCode("de"),
    val isChoosingFromLanguage: Boolean = false,
    val isChoosingToLanguage: Boolean = false,
    val error: TranslateError? = null,
    val history: List<UiHistoryItem> = emptyList()
)

//ViewModel держит state: TranslateState
//UI подписан на state -> каждое изменение автоматически обновляет экран
//когда пользователь меняет текст, выбирает язык, viewmodel создает новый объект TranslateState через copy
//UI получает новый state -> перерисовывается