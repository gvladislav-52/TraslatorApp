//
// Created by gvladislav-52 on 19.03.2026.
//

import Foundation
import shared
import Combine

@MainActor class IOSVoiceToTextViewModel: ObservableObject {
    private var parser: any VoiceToTextParser
    private let languageCode: String

    private let viewModel: VoiceToTextViewModel //КММ ЧАСТЬ
    @Published var state = VoiceToTextState(powerRatios: [], spokenText: "", canRecord: false, recordError: nil, displayState: nil) //стейт для обновления UI
    private var handle: Kotlinx_coroutines_coreDisposableHandle?//DisposableHandle? //подписка, где используется CommonFlow

    init(parser: VoiceToTextParser, languageCode: String) {
        self.parser = parser
        self.languageCode = languageCode
        self.viewModel = VoiceToTextViewModel(parser: parser, coroutineScope: nil)
        self.viewModel.onEvent(event: VoiceToTextEvent.PermissionResult(isGranted: true, isPermanentlyDeclined: false))
    }

    func onEvent(event: VoiceToTextEvent) {
        viewModel.onEvent(event: event) //отправка нужного имевента на MVI KMM
    }

    func startObserving() { //подписываемся на наш кастомный флоу KMM
        handle = viewModel.state.subscribe { [weak self] state in
            if let state {
                self?.state = state //на обновление стейта
            }
        }
    }

    func dispose() {    //отменяет подписку
        handle?.dispose()
        onEvent(event: VoiceToTextEvent.Reset())
    }
}

