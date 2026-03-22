package com.example.webinar_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.webinar_app.core.presentation.Routes
import com.example.webinar_app.translate.presentation.AndroidTranslateViewModel
import com.example.webinar_app.translate.presentation.TranslateScreen
import com.example.webinar_app.voice_to_text.presentation.AndroidVoiceToTextViewModel
import com.example.webinar_app.voice_to_text.presentation.VoiceToTextScreen
import com.example.webinar_app.translate.presentation.TranslateEvent
import com.example.webinar_app.voice_to_text.presentation.VoiceToTextEvent
import dagger.hilt.android.AndroidEntryPoint

@Composable
fun TranslatorTheme(    //определение темы приложение на устройстве
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {   //палитра цвета
        com.example.webinar_app.core.theme.darkColors
    } else {
        com.example.webinar_app.core.theme.lightColors
    }
    val SfProText = FontFamily( // определяем кастомный шрифт
        Font(
            resId = R.font.sf_pro_text_regular,
            weight = FontWeight.Normal
        ),
        Font(
            resId = R.font.sf_pro_text_medium,
            weight = FontWeight.Medium
        ),
        Font(
            resId = R.font.sf_pro_text_bold,
            weight = FontWeight.Bold
        ),
    )
    val typography = Typography(    //настраиваем размер, форму углов и тд
        h1 = TextStyle(
            fontFamily = SfProText,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        ),
        h2 = TextStyle(
            fontFamily = SfProText,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        ),
        h3 = TextStyle(
            fontFamily = SfProText,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp
        ),
        body1 = TextStyle(
            fontFamily = SfProText,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        ),
        body2 = TextStyle(
            fontFamily = SfProText,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp
        ),
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )

    MaterialTheme(  //Применяем тему ко всему UI, который находится внутри content
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

@AndroidEntryPoint  //Hilt может внедрять зависимости в эту Activity
class MainActivity : ComponentActivity() {  //точка входа в приложение с настройкой (темы, рут-навигации и тд)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TranslatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TranslateRoot()
                }
            }
        }
    }
}

@Composable //помечает функцию как UI-компонент, который можно отображать на экране
fun TranslateRoot() {   //компонент навигации андроид устройства
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.TRANSLATE //Создаём NavController для управления навигацией
    ) {
        composable(route = Routes.TRANSLATE) {
            val viewModel = hiltViewModel<AndroidTranslateViewModel>()  //di зависимость
            val state by viewModel.state.collectAsState()   //подписка на стейт флоу

            val voiceResult by it   //Если с экрана голосового ввода вернулся текст → отправляем его в ViewModel через событие
                .savedStateHandle
                .getStateFlow<String?>("voiceResult", null)
                .collectAsState()
            LaunchedEffect(voiceResult) {
                viewModel.onEvent(TranslateEvent.SubmitVoiceResult(voiceResult))
                it.savedStateHandle["voiceResult"] = null
            }

            TranslateScreen(    //Основной экран перевода
                state = state,
                onEvent = { event ->
                    when(event) {
                        is TranslateEvent.RecordAudio -> { //Событие RecordAudio → переходит на экран записи голоса
                            navController.navigate(
                                Routes.VOICE_TO_TEXT + "/${state.fromLanguage.language.langCode}"
                            )
                        }
                        else -> viewModel.onEvent(event)
                    }
                }
            )
        }
        composable( //экран распознавани речи
            route = Routes.VOICE_TO_TEXT + "/{languageCode}",
            arguments = listOf(
                navArgument("languageCode") {
                    type = NavType.StringType
                    defaultValue = "en"
                }
            )
        ) { backStackEntry ->
            val languageCode = backStackEntry.arguments?.getString("languageCode") ?: "en"
            val viewModel = hiltViewModel<AndroidVoiceToTextViewModel>()
            val state by viewModel.state.collectAsState()

            //Подключаем ViewModel для голосового ввода
            VoiceToTextScreen(
                state = state,
                languageCode = languageCode,
                onResult = { spokenText ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "voiceResult", spokenText
                    )
                   // Передаём обратно текст в предыдущий экран через savedStateHandle
                    navController.popBackStack()
                },
                onEvent = { event ->
                    when(event) {
                        is VoiceToTextEvent.Close -> {
                            navController.popBackStack()
                            //popBackStack() → закрываем экран записи голоса
                        }
                        else -> viewModel.onEvent(event)
                    }
                }
            )
        }
    }
}

