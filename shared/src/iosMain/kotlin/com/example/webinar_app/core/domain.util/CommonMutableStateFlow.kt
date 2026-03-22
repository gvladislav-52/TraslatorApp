package com.example.webinar_app.core.domain.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual open class CommonMutableStateFlow<T> actual constructor( //платформенная реализация на ios
    private val flow: MutableStateFlow<T>
): CommonStateFlow<T>(flow), MutableStateFlow<T> {  //наследование и интерфейс

    override var value: T   //перегрузка метода value из класса CommonStateFlow
        get() = super.value
        set(value) {
            flow.value = value
        }

    override val subscriptionCount: StateFlow<Int>
        get() = flow.subscriptionCount

    override fun compareAndSet(expect: T, update: T): Boolean {  //ч
        return flow.compareAndSet(expect, update)
    }

    @ExperimentalCoroutinesApi
    override fun resetReplayCache() {
        flow.resetReplayCache()
    }

    override fun tryEmit(value: T): Boolean {
        return flow.tryEmit(value)
    }

    override suspend fun emit(value: T) {
        flow.emit(value)
    }
}