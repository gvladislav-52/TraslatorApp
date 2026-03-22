import SwiftUI
import shared

struct ContentView: View {  //начальная view на которой расположены все view

    let appModule: AppModule

    var body: some View {
        ZStack {
            Color.background
                .ignoresSafeArea()
            TranslateScreen(    // основной экран (перевода)
                historyDataSource: appModule.historyDataSource, //передаем историю из kmm
                translateUseCase: appModule.translateUseCase,   //передаем use case для работы с переводами (сеть/локалка)
                parser: appModule.voiceParser   //парсер аудио звуков
            )
        }
    }
}
