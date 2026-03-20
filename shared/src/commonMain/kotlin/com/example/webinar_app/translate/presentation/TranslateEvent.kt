package com.example.webinar_app.translate.presentation

import com.example.webinar_app.core.presentation.UiLanguage

sealed class TranslateEvent {
    data class ChooseFromLanguage(val language: UiLanguage): TranslateEvent()   //пользователь выбрал язык источника
    data class ChooseToLanguage(val language: UiLanguage): TranslateEvent()     //пользователь выбрал язык перевода
    object StopChoosingLanguage: TranslateEvent()           //закрытия выбора языка
    object SwapLanguages: TranslateEvent()          //меняем языки местами
    data class ChangeTranslationText(val text: String): TranslateEvent()    //пользователь ввел/изменил текст
    object Translate: TranslateEvent()              //нажата кнопка перевода
    object OpenFromLanguageDropDown: TranslateEvent()   //открыть выпадающий список исходного языка
    object OpenToLanguageDropDown: TranslateEvent()     //открыть выпадающий список целевого языка
    object CloseTranslation: TranslateEvent()           //закрыть окно перевода
    data class SelectHistoryItem(val item: UiHistoryItem): TranslateEvent() //выбрать историю перевода
    object EditTranslation: TranslateEvent()    //Редактировать перевод
    object RecordAudio: TranslateEvent()    //Начало записи голоса
    data class SubmitVoiceResult(val result: String?): TranslateEvent()     //Результат распознавания голоса
    object OnErrorSeen: TranslateEvent()        //Пользователь увидел ошибку и закрыл ее
}

// запечатанный класс
// особый вид класса, который позволяет:
// 1. создвывть огрвниченное количество подклассов/событий
// его подклассы могут быть только внутри одного файла
// компилятор знает все варианты наследников

//все события перевода (клики, изменения текста, голосовой ввод) централизованы в одном классе

//data class - класс, который главным образом хранит данные
//object -  синглтон, один экземпляр на все приложение, используется если данные не нужны
//просто событие или действие