//
// Created by gvladislav-52 on 19.03.2026.
//

import SwiftUI
import shared

struct SmallLanguageIcon: View {    //иконка языка
    var language: UiLanguage
    var body: some View {
        Image(uiImage: UIImage(named: language.imageName.lowercased())!)
            .resizable()
            .frame(width: 30, height: 30)
    }
}
