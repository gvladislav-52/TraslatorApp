package com.example.webinar_app.translate.presentation

import com.example.webinar_app.core.domain.util.Resource
import com.example.webinar_app.core.domain.util.toCommonStateFlow
import com.example.webinar_app.core.presentation.UiLanguage
import com.example.webinar_app.translate.domain.history.HistoryDataSource
import com.example.webinar_app.translate.domain.translate.Translate
import com.example.webinar_app.translate.domain.translate.TranslateException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TranslateViewModel(   //контсруктор, отвечает за логику перевода, обработку событий UI, хранения состояний
    private val translate: Translate,
    private val historyDataSource: HistoryDataSource,
    private val coroutineScope: CoroutineScope? //корутина для запуска асинхронных операций
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main) //область для запуска корутин
    // если в конструкторе передали корутино, то используем ее, если нет то используется Диспатчер Main
    //все асинхронные операции запускаются внутри этого scope (translateJob = viewModelScope.launch)

    private val _state = MutableStateFlow(TranslateState())     //реактивнная стейт переменная, на которую подписан ui
    val state = combine(    //старый стейт и история подтягиваются, если есть изменения, то стейт меняется.
        _state,
        historyDataSource.getHistory(viewModelScope.coroutineContext)
    ) { state, history ->
        if(state.history != history) {
            state.copy(
                history = history.mapNotNull { item ->
                    UiHistoryItem(
                        id = item.id ?: return@mapNotNull null,
                        fromText = item.fromText,
                        toText = item.toText,
                        fromLanguage = UiLanguage.byCode(item.fromLanguageCode),
                        toLanguage = UiLanguage.byCode(item.toLanguageCode)
                    )
                }
            )
        } else state
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TranslateState())
        .toCommonStateFlow()    //превращает Flow в StateFlow чтобы UI мог подписываться

    private var translateJob: Job? = null // обьект группы корутины, который представляет запущенную асихнронную задачу.
    //Дает возможность отменять предыдущий перевод

    fun onEvent(event: TranslateEvent) {    //принимает события от UI
        when(event) {
            is TranslateEvent.ChangeTranslationText -> {    //WHEN обрабатывает все варианты событие, и делает какие то действия
                _state.update { it.copy(
                    fromText = event.text
                ) }
            }
            is TranslateEvent.ChooseFromLanguage -> {
                _state.update { it.copy(
                    isChoosingFromLanguage = false,
                    fromLanguage = event.language
                ) }
            }
            is TranslateEvent.ChooseToLanguage -> {
                val newState = _state.updateAndGet { it.copy(
                    isChoosingToLanguage = false,
                    toLanguage = event.language
                ) }
                translate(newState)
            }
            TranslateEvent.CloseTranslation -> {
                _state.update { it.copy(
                    isTranslating = false,
                    fromText = "",
                    toText = null
                ) }
            }
            TranslateEvent.EditTranslation -> {
                if(state.value.toText != null) {
                    _state.update { it.copy(
                        toText = null,
                        isTranslating = false
                    ) }
                }
            }
            TranslateEvent.OnErrorSeen -> {
                _state.update { it.copy(error = null) }
            }
            TranslateEvent.OpenFromLanguageDropDown -> {
                _state.update { it.copy(
                    isChoosingFromLanguage = true
                ) }
            }
            TranslateEvent.OpenToLanguageDropDown -> {
                _state.update { it.copy(
                    isChoosingToLanguage = true
                ) }
            }
            is TranslateEvent.SelectHistoryItem -> {
                translateJob?.cancel()
                _state.update { it.copy(
                    fromText = event.item.fromText,
                    toText = event.item.toText,
                    isTranslating = false,
                    fromLanguage = event.item.fromLanguage,
                    toLanguage = event.item.toLanguage
                ) }
            }
            TranslateEvent.StopChoosingLanguage -> {
                _state.update { it.copy(
                    isChoosingFromLanguage = false,
                    isChoosingToLanguage = false
                ) }
            }
            is TranslateEvent.SubmitVoiceResult -> {
                _state.update { it.copy(
                    fromText = event.result ?: it.fromText,
                    isTranslating = if(event.result != null) false else it.isTranslating,
                    toText = if(event.result != null) null else it.toText
                ) }
            }
            TranslateEvent.SwapLanguages -> {
                _state.update { it.copy(
                    fromLanguage = it.toLanguage,
                    toLanguage = it.fromLanguage,
                    fromText = it.toText ?: "",
                    toText = if(it.toText != null) it.fromText else null
                ) }
            }
            TranslateEvent.Translate -> translate(state.value)
            else -> Unit
        }
    }

    private fun translate(state: TranslateState) {  //проверяет, что перевож не выполняется или текст не пустой
        if(state.isTranslating || state.fromText.isBlank()) {
            return
        }

        translateJob = viewModelScope.launch {
            _state.update { it.copy(
                isTranslating = true
            ) }
            val result = translate.execute( // запускается асихронная корутина, вызывается сервис перевода Ktor
                fromLanguage = state.fromLanguage.language,
                fromText = state.fromText,
                toLanguage = state.toLanguage.language
            )
            when(result) {
                is Resource.Success -> {    //успешный результат
                    _state.update { it.copy(
                        isTranslating = false,
                        toText = result.data
                    ) }
                }
                is Resource.Error -> {      //неудача
                    _state.update { it.copy(
                        isTranslating = false,
                        error = (result.throwable as? TranslateException)?.error
                    ) }
                }
            }
        }
    }
}