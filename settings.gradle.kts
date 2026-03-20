pluginManagement {                  //Блок подключаемых плагинов        (ПЛАГИНЫ)
    repositories {
        google()                    //нужен для android gradle plugin, compose (часть андроида)
        mavenCentral()              //публичный репозиторий Java/Kotlin библиотек
        gradlePluginPortal()        //оф.репозиторий Gradle-плагинов (Kotlin, KMM plugins)
    }
}

dependencyResolutionManagement {    //Блок подключаемых библиотек (БИБЛИОТЕКА)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Webinar_App"
include(":composeApp")  //модуль проекта Android
include(":shared")      //модуль shared части проекта