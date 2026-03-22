//
// Created by gvladislav-52 on 19.03.2026.
//

import Foundation
import shared
import Speech   //аудио либы
import Combine

class IOSVoiceToTextParser: VoiceToTextParser, ObservableObject {   //наследуется от кмм части

    private let _state = IOSMutableStateFlow(   //хранит текущее состояние распознавания
        initialValue: VoiceToTextParserState(result: "", error: nil, powerRatio: 0.0, isSpeaking: false)
    )
    var state: CommonStateFlow<VoiceToTextParserState> { _state }

    private var micObserver = MicrophonePowerObserver()     //отслеживание громкости микрофона
    var micPowerRatio: Published<Double>.Publisher { micObserver.$micPowerRatio }   //публичная публикпция громкости микрофона
    private var micPowerCancellable: AnyCancellable?    //отмена подписки на обновление микрофона

    private var recognizer: SFSpeechRecognizer? //распозныется речь клиента
    private var audioEngine: AVAudioEngine? //захватывает микрофон поток
    private var inputNode: AVAudioInputNode?    //нода, входной узел микрофона
    private var audioBufferRequest: SFSpeechAudioBufferRecognitionRequest?  //буфер для распознавания
    private var recognitionTask: SFSpeechRecognitionTask?   //задача по распознаванию
    private var audioSession: AVAudioSession?   //управляет аудио сессией устройства

    func cancel() {
        // Not needed on iOS
    }

    func reset() {  //останавливаем запись, сбрасываем стейт на начальное состояние
        self.stopListening()
        _state.value = VoiceToTextParserState(result: "", error: nil, powerRatio: 0.0, isSpeaking: false)
    }

    func startListening(languageCode: String) { //начало записи, сбрасываем ошибку
        updateState(error: nil)

        let chosenLocale = Locale.init(identifier: languageCode)    //выбираем язык распознования (Если язык не поддерживается, используем английский)
        let supportedLocale = SFSpeechRecognizer.supportedLocales().contains(chosenLocale) ? chosenLocale : Locale.init(identifier: "en-US")
        self.recognizer = SFSpeechRecognizer(locale: supportedLocale)

        guard recognizer?.isAvailable == true else {    // Проверяем, доступен ли распознаватель на устройстве
            updateState(error: "Speech recognizer is not available")
            return
        }

        audioSession = AVAudioSession.sharedInstance()  //создаем сессию для распознавания

        self.requestPermissions { [weak self] in    //задаем регвест на пермишен
            self?.audioBufferRequest = SFSpeechAudioBufferRecognitionRequest()  //создаем буфер на распознавания речи
            
            guard let audioBufferRequest = self?.audioBufferRequest else {
                return
            }

            self?.recognitionTask = self?.recognizer?.recognitionTask(with: audioBufferRequest) { [weak self] (result, error) in
                guard let result = result else {    //создаем задачу на распознавания речи
                    //каждое обновление записи идет сюда
                    self?.updateState(error: error?.localizedDescription)
                    return
                }

                //если распознавание окончено, обновляем стейт
                if result.isFinal {
                    self?.updateState(result: result.bestTranscription.formattedString)
                }
            }

            self?.audioEngine = AVAudioEngine() //инициализируем поток микрофона
            self?.inputNode = self?.audioEngine?.inputNode

            let recordingFormat = self?.inputNode?.outputFormat(forBus: 0)  // подключаем микрофон к буферу распознования речи
            self?.inputNode?.installTap(onBus: 0, bufferSize: 1024, format: recordingFormat) { buffer, _ in
                self?.audioBufferRequest?.append(buffer)
            }

            self?.audioEngine?.prepare()    //настраивааем сессию для записи и воспроизведения

            do {
                try self?.audioSession?.setCategory(.playAndRecord, mode: .spokenAudio, options: .duckOthers)   //временно снижает громкость других приложений (например музыка)
                try self?.audioSession?.setActive(true, options: .notifyOthersOnDeactivation)

                self?.micObserver.startObserving() //Запускаем наблюдение за микрофоном
                //каждые 1/20 сек обновляем громкость

                try self?.audioEngine?.start()  //запускаем поток аудио записи

                self?.updateState(isSpeaking: true) //устанавливаем состояние, на ГОВОРИТ

                self?.micPowerCancellable = self?.micPowerRatio //подписываемся на громкость микрофона, для обновления состояния, и дальнейшей отмены
                    .sink { [weak self] ratio in
                        self?.updateState(powerRatio: ratio)
                    }
            } catch {
                self?.updateState(error: error.localizedDescription, isSpeaking: false)
            }
        }
    }

    func stopListening() {  //Останавливаем запись, аудио и микрофон
        self.updateState(isSpeaking: false)

        micPowerCancellable = nil
        micObserver.release()

        audioBufferRequest?.endAudio()
        audioBufferRequest = nil

        audioEngine?.stop()

        inputNode?.removeTap(onBus: 0)

        try? audioSession?.setActive(false)
        audioSession = nil
    }

    private func requestPermissions(onGranted: @escaping () -> Void) {  //проверка на разрешение микрофона и распознования речи
        audioSession?.requestRecordPermission { [weak self] wasGranted in
            if !wasGranted {
                self?.updateState(error: "You need to grant permission to record your voice.")
                self?.stopListening()
                return
            }
            SFSpeechRecognizer.requestAuthorization { [weak self] status in
                DispatchQueue.main.async {
                    if status != .authorized {
                        self?.updateState(error: "You need to grant permission to transcribe audio.")
                        self?.stopListening()
                        return
                    }
                    onGranted()
                }
            }
        }
    }

    //метод для обновления стейта
    private func updateState(result: String? = nil, error: String? = nil, powerRatio: CGFloat? = nil, isSpeaking: Bool? = nil) {
        let currentState = _state.value
        _state.value = VoiceToTextParserState(
            result: result ?? currentState?.result ?? "",
            error: error ?? currentState?.error,
            powerRatio: Float(powerRatio ?? CGFloat(currentState?.powerRatio ?? 0.0)),
            isSpeaking: isSpeaking ?? currentState?.isSpeaking ?? false
        )
    }

}

