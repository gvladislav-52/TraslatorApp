package com.example.webinar_app.voice_to_text.domain

data class VoiceToTextParserState(
    val result: String = "",
    val error: String? = null,
    val powerRatio: Float = 0f,
    val isSpeaking: Boolean = false
)

// модель для работы с войсом