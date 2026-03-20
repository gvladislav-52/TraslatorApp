package com.example.webinar_app.translate.data.translate

import kotlinx.serialization.Serializable
@Serializable
data class TranslatedResponse(
    val data: TranslationData
)

@Serializable
data class TranslationData(
    val translations: List<TranslationItem>
)

@Serializable
data class TranslationItem(
    val translatedText: String
)

//Модель Данных, которые приходят с API для парсинга их.