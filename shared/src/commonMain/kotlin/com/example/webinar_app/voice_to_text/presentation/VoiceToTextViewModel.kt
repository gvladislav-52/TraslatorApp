package com.example.webinar_app.voice_to_text.presentation

import com.example.webinar_app.core.domain.util.toCommonStateFlow
import com.example.webinar_app.voice_to_text.domain.VoiceToTextParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VoiceToTextViewModel(
    private val parser: VoiceToTextParser,  //модель войса
    coroutineScope: CoroutineScope? = null  //асинхронное выполнение
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main) //корутина

    private val _state = MutableStateFlow(VoiceToTextState())   // приватный стейт, который хранит текущее состояние экрана
    val state = _state.combine(parser.state) { state, voiceResult ->    //публичный поток, на который подписан UI
        state.copy(
            spokenText = voiceResult.result,
            recordError = if (state.canRecord) {
                voiceResult.error
            } else {
                "Can't record without permission"
            },
            displayState = when {
                !state.canRecord || voiceResult.error != null -> DisplayState.ERROR
                voiceResult.result.isNotBlank() && !voiceResult.isSpeaking -> {
                    DisplayState.DISPLAYING_RESULTS
                }
                voiceResult.isSpeaking -> DisplayState.SPEAKING
                else -> DisplayState.WAITING_TO_TALK
            }
        )
    }
        //делает поток активным, чтобы держать текущее состояния для ui
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VoiceToTextState())
        .toCommonStateFlow()    //преобразует чтоб поток стейт совместимым с ios, чтобы SwiftUI тоже мог подписываться

    init {
        viewModelScope.launch { //создает корутину, которая каждую 50 мс проверяет, идет ли речь (SPEAKING)
            while (true) {
                if (state.value.displayState == DisplayState.SPEAKING) {    //если да, обновляет уровни громкости микрофона для UI
                    _state.update {
                        it.copy(
                            powerRatios = it.powerRatios + parser.state.value.powerRatio
                        )
                    }
                }
                delay(50L)
            }
        }
    }

    fun onEvent(event: VoiceToTextEvent) {  //метод который принимает события от UI и меняет состояние
        when (event) {
            is VoiceToTextEvent.PermissionResult -> {   //ивент обновления разрешения на запись
                _state.update { it.copy(canRecord = event.isGranted) }
            }
            VoiceToTextEvent.Reset -> {     //ивент сбрасывает парсер и состояние _state
                parser.reset()
                _state.update { VoiceToTextState() }
            }
            is VoiceToTextEvent.ToggleRecording -> toggleRecording(event.languageCode)  // игнорирует остальные события
            else -> Unit
        }
    }

    private fun toggleRecording(languageCode: String) {     //Сбрасывает текущие уровни микрофона
        _state.update { it.copy(powerRatios = emptyList()) }    //отменяет текущий процесс распознавания
        parser.cancel()
        if (state.value.displayState == DisplayState.SPEAKING) {    //если сейчас идет речь, остнавливает запись
            parser.stopListening()
        } else {
            parser.startListening(languageCode)     //иначе начинает запись с выбранным языком
        }
    }
}