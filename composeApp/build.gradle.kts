plugins {                       //подключение плагинов
    alias(libs.plugins.android.application) //плагин работы андроид приложения (apk, запуск)
    alias(libs.plugins.kotlin)          //позволяет писать код на котлине
    alias(libs.plugins.ksp)             //генерация кода, через @Inject
    alias(libs.plugins.hilt)               //DI
    alias(libs.plugins.kotlin.serialization)   //JSON
}

android {
    namespace = "com.example.webinar_app"   //пакед для классов, R, генерации кода
    compileSdk = 34     //таргет запуска
    defaultConfig {
        applicationId = "com.example.webinar_app"
        minSdk = 24     //диапазон устройств
        targetSdk = 34
        versionCode = 1
        versionName = "1.0" //версия приложения

        testInstrumentationRunner = "com.example.webinar_app.TestHiltRunner"    //кастомный раннер для тестов с Hilt
    }
    compileOptions {    //задаем какую версию Java используем
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {     //версия JVM под которую компилируется Kotlin
        jvmTarget = "21"
    }
    buildFeatures {  //вкл использование Jetpack Compose
        compose = true
    }
    composeOptions {    //версия компилятора Compose
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packagingOptions {
        resources { //решает конфликты при сборки
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {    //настройки release версии
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {  //Зависимости для андроида
    implementation(project(":shared"))  //подключаем общий модуль для работы с бизнес-логикой
    implementation(platform(libs.androidx.compose.bom)) //управление версией компоуза
    implementation(libs.androidx.compose.ui)    //все необходимые зависимости для интерфейса
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)    //зависимость навигации между экранами
    implementation(libs.coil.compose)       //загрузка изображений

    implementation(libs.hilt.android)   //di - автоматическое создание зависимостей
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.ktor.android)   //работа с сетью

    androidTestImplementation(platform(libs.androidx.compose.bom))  // аналогичные зависимости для тестирования андроида
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.test.rule)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    kspAndroidTest(libs.hilt.android.compiler)
    androidTestImplementation(libs.hilt.testing)
}