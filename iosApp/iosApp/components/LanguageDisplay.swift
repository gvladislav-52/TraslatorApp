//
// Created by gvladislav-52 on 19.03.2026.
//

import SwiftUI
import shared

struct LanguageDisplay: View {      //отображения использоваемого языка (иконка + текст)
    var language: UiLanguage

    var body: some View {
        HStack {
            SmallLanguageIcon(language: language)
                .padding(.trailing, 5)
            Text(language.language.langName)
                .foregroundColor(.lightBlue)
        }
    }
}

struct LanguageDisplay_Previews: PreviewProvider {
    static var previews: some View {
        LanguageDisplay(
            language: UiLanguage(language: .german, imageName: "german")
        )
    }
}

