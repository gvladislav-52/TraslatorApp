plugins {
    alias(libs.plugins.kotlin) apply false  //Плагин доступен, но не применяется здесь      //Kotlin
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false                                         //Android Library
    alias(libs.plugins.sqldelight) apply false                                              //Database SQLDelight
    alias(libs.plugins.hilt) apply false                                                    //DI
    alias(libs.plugins.kotlin.multiplatform) apply false                                    //KMM
    alias(libs.plugins.kotlin.native.cocoapods) apply false                                 //IOS Integration
    alias(libs.plugins.ksp) apply false                                                     //Kotlin Symbol Processing (KSP)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

// Это кастомная задача для gradle, на ужаление файлов, удаляет кеш сборки, удаляет сгенерированные файлы
//используется, если что то сломалось, перед чистой бсоркой, перед ci.


//Это главный Gradle-файл проекта
// тут объявлят плагины (но не применяют их)
// задаются общие вещи для всех модулей

//тк как мы их тут задаем не подключеам, нет дублирования версий плагинов
// подключаем их в модулях composeApp, shared





