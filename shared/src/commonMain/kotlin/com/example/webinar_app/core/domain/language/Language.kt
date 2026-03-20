package com.example.webinar_app.core.domain.language

enum class Language(
    val langCode: String,   //код языка
    val langName: String    //читаемое название языка
) {
    //каждый элемент enum создается с кожом и названием
    ENGLISH("en", "English"),
    ARABIC("ar", "Arabic"),
    AZERBAIJANI("az", "Azerbaijani"),
    CHINESE("zh", "Chinese"),
    CZECH("cs", "Czech"),
    DANISH("da", "Danish"),
    DUTCH("nl", "Dutch"),
    FINNISH("fi", "Finnish"),
    FRENCH("fr", "French"),
    GERMAN("de", "German"),
    GREEK("el", "Greek"),
    HEBREW("he", "Hebrew"),
    HINDI("hi", "Hindi"),
    HUNGARIAN("hu", "Hungarian"),
    INDONESIAN("id", "Indonesian"),
    IRISH("ga", "Irish"),
    ITALIAN("it", "Italian"),
    JAPANESE("ja", "Japanese"),
    KOREAN("ko", "Korean"),
    PERSIAN("fa", "Persian"),
    POLISH("pl", "Polish"),
    PORTUGUESE("pt", "Portuguese"),
    RUSSIAN("ru", "Russian"),
    SLOVAK("sk", "Slovak"),
    SPANISH("es", "Spanish"),
    SWEDISH("sv", "Swedish"),
    TURKISH("tr", "Turkish"),
    UKRAINIAN("uk", "Ukrainian");

    companion object {  //это аналог статических методов
        fun byCode(code: String): Language {
            return values().find { it.langCode == code }
                ?: throw IllegalArgumentException("Invalid or unsupported language code")
        }
    }
    // позволяет получить Language по его коду
    // Если передан неподдерживаемый код - выбросится исключение
}