plugins {                                               //подключение возможных плагинов
    alias(libs.plugins.kotlin.multiplatform)            //вкл KMM
    alias(libs.plugins.kotlin.native.cocoapods)         //связь ios через cocoapods
    alias(libs.plugins.android.library)                 //делает модуль android-библиотекой
    alias(libs.plugins.kotlin.serialization)        //работа с json
    alias(libs.plugins.sqldelight)                  //работс с бд
}
//Котлин блок, основная часть КММ
//Здесь описываются платформы, общий код, зависимости
kotlin {
    tasks.create("testClasses")

    android()   //Платформы, под какие устройства собирается проект
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {     //мост между Kotlin и iOS
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage" //метаинфорамция
        version = "1.0"
        ios.deploymentTarget = "14.1"                   //минимальная версия iOS
        podfile = project.file("../iosApp/Podfile") //путь к IOS проекту
        framework {                     //создание фреймворка взаимодействия shared.framework
            isStatic = false
            baseName = "shared"
        }
    }

    sourceSets {                        //Ядро всей архитектуры КММ
                                    //тут расписываются все наши компоненты shared модуля + его записимости
        val commonMain by getting {     //общий код для ВСЕХ платформ
            dependencies {
                implementation(libs.bundles.ktor)   //зависимость на сеть ktor
                implementation(libs.sqldelight.runtime) //зависимость на бд sqldelight
                implementation(libs.sqldelight.coroutines.extensions)
                implementation(libs.kotlin.date.time)   //зависимость на дату и время
            }
        }
        val commonTest by getting {     //модуль для тестирования
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.assertk)
                implementation(libs.turbine)
            }
        }
        val androidMain by getting {    //код для андроида и его зависимостей
            dependencies {
                implementation(libs.ktor.android)
                implementation(libs.sqldelight.android.driver)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {   //код для ios устройств
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)  //все ios версии будут использовать этот код для работы
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation(libs.ktor.ios)   // зависимость для ios
                implementation(libs.sqldelight.native.driver)
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {       //пример для теста
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {   //настройка андроид части
    namespace = "com.example.webinar_app" //имя package.name
    compileSdk = 34                         //версии андроида, минимальная и для компиляции
    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }

    compileOptions {                //установка версии Java
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

sqldelight {    //конфигурация базы данных
    databases {
        create("TranslateDatabase") {
            packageName.set("com.example.webinar_app.database")
        }
        //создается база данных TranslateDatabase в нужном нам пакете, например database
        //SQLDelight позволяет нам генерировать Котлин код в SQL
    }
}