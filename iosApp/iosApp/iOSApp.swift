import SwiftUI
import shared

@main
struct iOSApp: App {        //точка входа ios приложения
    private var appModule: any AppModule = AppModuleImpl(parser: IOSVoiceToTextParser())    //DI задается зависимость приложения, с типом которым будем работать

    var body: some Scene {
        WindowGroup {
            NavigationView {        //задаем иерахрию с навигационной менюшкой
                ContentView(appModule: appModule)
            }
        }
    }
}
