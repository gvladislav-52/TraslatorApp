package com.example.webinar_app.core.domain.util

import kotlinx.coroutines.flow.StateFlow

actual class CommonStateFlow<T> actual constructor(
    private val flow: StateFlow<T>
) : StateFlow<T> by flow

//платформенная реализация на андроиде класса наследника от StateFlow