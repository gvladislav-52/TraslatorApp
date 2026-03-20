package com.example.webinar_app.core.domain.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

expect class CommonStateFlow<T>(flow: StateFlow<T>): StateFlow<T>

fun <T> StateFlow<T>.toCommonStateFlow() = CommonStateFlow(this)
//расширение на обычный StateFlow, позволяет легко преобразовать обычный StateFlow в CommonStateFlow
