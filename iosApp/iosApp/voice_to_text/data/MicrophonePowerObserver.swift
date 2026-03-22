//
// Created by gvladislav-52 on 19.03.2026.
//

import Foundation
import shared
import Speech
import Combine

class MicrophonePowerObserver: ObservableObject {   //следит за уровнем громкости микрофона
    private var cancellable: AnyCancellable? = nil  //возмодность отменить
    private var audioRecorder: AVAudioRecorder? = nil

    @Published private(set) var micPowerRatio = 0.0 //тек значение микро

    private let powerRatioEmissionsPerSecond = 20.0 //частота обновления громкости

    func startObserving() {
        do {
            let recorderSettings: [String: Any] = [
                AVFormatIDKey: NSNumber(value: kAudioFormatAppleLossless),
                AVNumberOfChannelsKey: 1
            ]
            //создаем фальшивую аудиозапись /dev/nul, включаем измерение громкости (Запись идёт, но файл не сохраняется)
            let recorder = try AVAudioRecorder(url: URL(fileURLWithPath: "/dev/null", isDirectory: true), settings: recorderSettings)
            recorder.isMeteringEnabled = true
            recorder.record()
            self.audioRecorder = recorder

            self.cancellable = Timer.publish(
                    every: 1.0 / powerRatioEmissionsPerSecond,
                    tolerance: 1.0 / powerRatioEmissionsPerSecond,
                    on: .main,
                    in: .common
                )
                .autoconnect()
                .sink { [weak self] _ in    //каждые 1/20 обновляем громкость
                    recorder.updateMeters()

                    let powerOffset = recorder.averagePower(forChannel: 0)
                    if powerOffset < -50 {
                        self?.micPowerRatio = 0.0
                    } else {
                        let normalizedOffset = CGFloat(50 + powerOffset) / 50
                        self?.micPowerRatio = normalizedOffset
                    }
                }
        } catch {
            print("An error occurred when observing microphone power: \(error.localizedDescription)")
        }
    }

    func release() {    //останавливаем наблюдение
        cancellable = nil

        audioRecorder?.stop()
        audioRecorder = nil

        micPowerRatio = 0.0
    }
}
