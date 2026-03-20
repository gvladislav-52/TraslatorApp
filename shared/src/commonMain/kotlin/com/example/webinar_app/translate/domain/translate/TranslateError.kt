package com.example.webinar_app.translate.domain.translate

enum class TranslateError {     //enum перечисление ошибок, которые можно выкинуть
    SERVICE_UNAVAILABLE,
    CLIENT_ERROR,
    SERVER_ERROR,
    UNKNOWN_ERROR
}

class TranslateException(val error: TranslateError): Exception(
    "An error occurred when translating: $error"
)
// Этот класс наследует стандартный Exeption и добавляет свое поле ошибки error: TranslateError
// создает объект ошибки с текстом