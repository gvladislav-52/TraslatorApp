//
// Created by gvladislav-52 on 19.03.2026.
//

import Foundation
import AVFoundation

struct TextToSpeech {   //файл утилита для озвучивания текста

    private let synthesizer = AVSpeechSynthesizer() //синтез речи, объект который генерирует речь и воспроизвонид его

    func speak(text: String, language: String) {    //передаем что сказать и на каком языке
        let utterance = AVSpeechUtterance(string: text) //устанавливаем текст
        utterance.voice = AVSpeechSynthesisVoice(language: language)    //устанавливаем язык распознавания (любой какой поддерживает ios)
        utterance.volume = 1    //выставляем громкость
        synthesizer.speak(utterance)    //воспроизводим
    }
}
