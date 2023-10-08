package nay.kirill.telegram_kmp.mini_app.compose

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import nay.kirill.telegram_kmp.mini_app.compose.theme.ColorsConverter
import nay.kirill.telegram_kmp.mini_app.compose.theme.ThemeHandler
import org.jetbrains.skiko.wasm.onWasmReady

/**
 * Use [telegramWebApp] to provide Compose content of mini app.
 *
 * @param colorsConverter handles logic of converting telegram colors and color scheme (light or dark).
 * Pass custom colorsConverter to provide your own colors based on your design system.
 * It's recommended to create your own colors based on themeParams to provide smooth UX.
 *
 * @param themeHandler handles telegram theme switching (e.g light and dark mode switching).
 * Pass custom themeHandler in case you have your own logic of theme switching.
 *
 * @param content is compose content of your mini app.
 *
 */
@OptIn(ExperimentalComposeUiApi::class)
fun telegramWebApp(
        colorsConverter: ColorsConverter = ColorsConverter.Default(),
        themeHandler: ThemeHandler = ThemeHandler.Default(colorsConverter),
        content: @Composable () -> Unit
) {
    onWasmReady {
        CanvasBasedWindow {
            val colors by themeHandler.colors.collectAsState()

            MaterialTheme(
                colors = colors,
                content = content
            )
        }
    }
}
