package nay.kirill.telegram_kmp.mini_app.compose.theme

import androidx.compose.material.Colors
import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import nay.kirill.telegram_kmp.mini_app.webApp.EventType
import nay.kirill.telegram_kmp.mini_app.webApp.webApp

/**
 * Handles theme changes of mini app. Default implementation configure colors based on telegram app theme changes.
 */
interface ThemeHandler {

    val colors: StateFlow<Colors>

    @Stable
    class Default(colorsConverter: ColorsConverter): ThemeHandler {

        private val _colors = MutableStateFlow(
            colorsConverter.convert(TelegramColors.fromWebApp(), webApp.colorScheme)
        )
        override val colors: StateFlow<Colors> = _colors

        init {
            webApp.addEventHandler(EventType.THEME_CHANGED) {
                _colors.value = colorsConverter.convert(
                    TelegramColors.fromWebApp(), webApp.colorScheme
                )
            }
        }

    }

}
