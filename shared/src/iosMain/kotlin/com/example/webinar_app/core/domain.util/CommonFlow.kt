package com.example.webinar_app.core.domain.util

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

actual open class CommonFlow<T> actual constructor( //actual - платформенная реализация класса CommonFlow для KMM
    private val flow: Flow<T>   // приватный параметры в конструкторе
): Flow<T> by flow {    // делегирвоание интерфейса

    fun subscribe(      //Позволяет подписаться на изменение Flow и получать элементы в onCollect
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,    //объект, который определяет, на каком потоке или пуле потоков выполняется корутина
        onCollect: (T) -> Unit
    ): kotlinx.coroutines.DisposableHandle {
        val job = coroutineScope.launch(dispatcher) {   //запускает корутину в указанной области и  диспетчере
            flow.collect(onCollect)
        }
        return DisposableHandle { job.cancel() }    // Возвращает DisposableHandle, чтобы можно было отписаться
                                                    // и отменить корутину, когда больше не нужно получать события
    }

    fun subscribe(      //упрощенная версия быстрой подписки, использоуется глобалньый скоуп
        onCollect: (T) -> Unit
    ): kotlinx.coroutines.DisposableHandle {
        return subscribe(
            coroutineScope = GlobalScope,
            dispatcher = Dispatchers.Main,
            onCollect = onCollect
        )
    }
}

