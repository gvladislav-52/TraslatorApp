package com.example.webinar_app.core.domain.util

import kotlinx.coroutines.flow.Flow

actual class CommonFlow<T> actual constructor(
    private val flow: Flow<T>
) : Flow<T> by flow

//платформенная реализация флоу на адроиде (реализовывать не требуется так как котлин и так понимает этов все)