package com.example.webinar_app.translate.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.webinar_app.translate.domain.history.HistoryDataSource
import com.example.webinar_app.translate.domain.translate.Translate
import com.example.webinar_app.translate.presentation.TranslateEvent
import com.example.webinar_app.translate.presentation.TranslateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AndroidTranslateViewModel @Inject constructor(
    private val translate: Translate,
    private val historyDataSource: HistoryDataSource
) : ViewModel() {

    private val viewModel by lazy { //объект создаётся только при первом обращении.
        TranslateViewModel(
            translate = translate,
            historyDataSource = historyDataSource,
            coroutineScope = viewModelScope //scope для корутин на Android, чтобы KMM корутины знали, где запускать асинхронную работу.
        )
    }

    val state = viewModel.state //стейт для обновления ui

    fun onEvent(event: TranslateEvent) {    //отправка ивента
        viewModel.onEvent(event)
    }
}