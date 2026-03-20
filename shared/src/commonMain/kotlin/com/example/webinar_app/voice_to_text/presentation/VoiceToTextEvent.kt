package com.example.webinar_app.voice_to_text.presentation

sealed class VoiceToTextEvent {
    object Close: VoiceToTextEvent()    // событие закрытия экрана или голосового ввода
    data class PermissionResult(        // результат запроса разрешений
        val isGranted: Boolean,         //
        val isPermanentlyDeclined: Boolean  //Начать или остановить запись, с указанием языка
    ): VoiceToTextEvent()
    data class ToggleRecording(val languageCode: String): VoiceToTextEvent()
    object Reset: VoiceToTextEvent()        //Сброс состояния голосового ввода
}
