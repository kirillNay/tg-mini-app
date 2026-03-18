package com.kirillNay.telegram.miniapp.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import com.kirillNay.telegram.miniapp.webApp.EventType
import com.kirillNay.telegram.miniapp.webApp.webApp
import kotlinx.browser.document
import org.jetbrains.skiko.wasm.onWasmReady

/**
 * Use [telegramWebApp] to provide Compose content of mini app.
 *
 * @param content is compose content of your mini app.
 *
 */
@OptIn(ExperimentalComposeUiApi::class)
fun telegramWebApp(
    content: @Composable (TelegramStyle) -> Unit
) {
    onWasmReady {
        ComposeViewport(document.body!!) {
            var paddings by remember { mutableStateOf(ViewPort(webApp.viewportHeight.dp, webApp.viewportStableHeight.dp)) }
            var colors by remember { mutableStateOf(TelegramColors.fromWebApp()) }

            LaunchedEffect(true) {
                webApp.addEventHandler(EventType.VIEWPORT_CHANGED) {
                    paddings = ViewPort(webApp.viewportHeight.dp, webApp.viewportStableHeight.dp)
                }

                webApp.addEventHandler(EventType.THEME_CHANGED) {
                    colors = TelegramColors.fromWebApp()
                }
            }

            content(TelegramStyle(paddings, colors))
        }
    }
}
