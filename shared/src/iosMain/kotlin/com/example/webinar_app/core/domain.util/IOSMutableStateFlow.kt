package com.example.webinar_app.core.domain.util

import kotlinx.coroutines.flow.MutableStateFlow

class IOSMutableStateFlow<T>(
    initialValue: T
): CommonMutableStateFlow<T>(MutableStateFlow(initialValue))

//стейт для ios mutable state flow, который просто возвращает другой тип commonmutable уже с заданым параметром mutable