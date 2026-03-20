package com.example.webinar_app.voice_to_text.domain

import com.example.webinar_app.core.domain.util.CommonStateFlow

interface VoiceToTextParser {   //интерфейс с методами
    val state: CommonStateFlow<VoiceToTextParserState>
    fun startListening(languageCode: String)
    fun stopListening()
    fun cancel()
    fun reset()
}