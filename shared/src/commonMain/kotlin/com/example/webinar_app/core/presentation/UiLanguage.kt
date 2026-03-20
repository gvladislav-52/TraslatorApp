package com.example.webinar_app.core.presentation

import com.example.webinar_app.core.domain.language.Language

expect class UiLanguage {
    val language: Language  //хранит объект
    companion object {      //статический метод
        fun byCode(langCode: String): UiLanguage    //функция поиска по коду языка
        val allLanguages: List<UiLanguage>      //список всех языков
    }
}

// Он объявляет класс без реализации, которая зависит от платформы

//UiLanguage - это обертка для класса Language, но предназначена для UI
//Для того, чтобы показывать язык в списке выбора или отображать название