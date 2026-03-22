package com.example.webinar_app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp   //Di

@HiltAndroidApp //генерация Hilt компонентов, использования @Inject, инит зависимостей
class TranslateApp: Application()   //точка входа на Андроид