package com.example.webinar_app.translate.domain.translate

import com.example.webinar_app.core.domain.language.Language

interface TranslateClient {
    suspend fun translate(
        fromLanguage: Language,
        fromText: String,
        toLanguage: Language
    ): String
}