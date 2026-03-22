package com.example.webinar_app.core.presentation

import com.example.webinar_app.core.domain.language.Language
actual class UiLanguage(        //платформенная реализация на ios
    actual val language: Language,  //код языка
    val imageName: String           //имя языка
) {
    actual companion object {       //платформанная реализация статического метода
        actual fun byCode(langCode: String): UiLanguage {
            return allLanguages.find { it.language.langCode == langCode }   //возвращаем все возможные языки, где код совпадает
                ?: throw IllegalArgumentException("Invalid or unsupported language code")
        }

        actual val allLanguages: List<UiLanguage>       //метод возвращения списка языков из бд для отображения UI
            get() = Language.values().map { language ->     //создаем массив языков UI для отображения
                UiLanguage(
                    language = language,
                    imageName = language.langName.lowercase()
                )
            }
    }
}