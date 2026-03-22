//
// Created by gvladislav-52 on 19.03.2026.
//

import SwiftUI
import shared

struct VoiceToTextScreen: View {
    private let onResult: (String) -> Void

    @ObservedObject var viewModel: IOSVoiceToTextViewModel  //следим на стейтом для обновления экрана
    private let parser: any VoiceToTextParser
    private let languageCode: String

    @Environment(\.presentationMode) var presentation

    init(onResult: @escaping (String) -> Void, parser: any VoiceToTextParser, languageCode: String) {
        self.onResult = onResult
        self.parser = parser
        self.languageCode = languageCode
        self.viewModel = IOSVoiceToTextViewModel(parser: parser, languageCode: languageCode)
    }

    var body: some View {
        VStack {
            Spacer()

            mainView

            Spacer()

            HStack {
                Spacer()
                VoiceRecorderButton(
                    displayState: viewModel.state.displayState ?? .waitingToTalk,
                    onClick: {
                        if viewModel.state.displayState != .displayingResults {
                            viewModel.onEvent(event: VoiceToTextEvent.ToggleRecording(languageCode: languageCode))
                        } else {
                            onResult(viewModel.state.spokenText)   //возвращаем текст и закрываем экран
                            self.presentation.wrappedValue.dismiss()
                        }
                    }
                )
                if viewModel.state.displayState == .displayingResults {
                    Button(action: {
                        viewModel.onEvent(event: VoiceToTextEvent.ToggleRecording(languageCode: languageCode))
                    }) {
                        Image(systemName: "arrow.clockwise")    //повторить результат
                            .foregroundColor(.lightBlue)
                    }
                }
                Spacer()
            }
        }
        .onAppear {
            viewModel.startObserving()
        }
        .onDisappear {
            viewModel.dispose()
        }
        .background(Color.background)
    }

    var mainView: some View { //отображение на экране в зависимости от стейта
        if let displayState = viewModel.state.displayState {
            switch displayState {
            case .waitingToTalk:    //ожидание
                return AnyView(
                    Text("Click record and start talking.")
                        .font(.title2)
                )
            case .displayingResults:    //отображение результата
                return AnyView(
                    Text(viewModel.state.spokenText)
                        .font(.title2)
                )
            case .error:    //отображение ошибки, например не выдан пермишен
                return AnyView(
                    Text(viewModel.state.recordError ?? "Unknown error")
                        .font(.title2)
                        .foregroundColor(.red)
                )
            case .speaking: //отображения когда что то говорим, и аудиодорожка нас слушает
                return AnyView(
                    VoiceRecorderDisplay(
                        powerRatios: viewModel.state.powerRatios.map { Double(truncating: $0) }
                    )
                    .frame(maxHeight: 100)
                    .padding()
                )
            default: return AnyView(EmptyView())
            }
        } else {
            return AnyView(EmptyView())
        }
    }
}

