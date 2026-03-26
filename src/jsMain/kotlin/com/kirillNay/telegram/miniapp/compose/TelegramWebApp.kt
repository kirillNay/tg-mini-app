package com.kirillNay.telegram.miniapp.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import kotlinx.browser.window
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
    val startTimestampMillis = window.performance.now()

    onWasmReady {
        ComposeViewport(document.body!!) {
            val timeToFirstFrameLogger = remember { TimeToFirstFrameLogger(startTimestampMillis) }
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

            SideEffect {
                timeToFirstFrameLogger.onFirstComposeCommit()
            }

            content(TelegramStyle(paddings, colors))
        }
    }
}

private class TimeToFirstFrameLogger(
    private val startTimestampMillis: Double,
) {
    private var isScheduled = false

    fun onFirstComposeCommit() {
        if (isScheduled) return
        isScheduled = true

        // Two RAF hops ensure the log runs after the first browser paint.
        window.requestAnimationFrame {
            window.requestAnimationFrame {
                val timeToFirstFrameMillis = window.performance.now() - startTimestampMillis
                console.log("[tg-mini-app] Time To First Frame: ${timeToFirstFrameMillis.toInt()} ms")
            }
        }
    }
}
