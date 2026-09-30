package com.kirillNay.telegram.miniapp.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import com.kirillNay.telegram.miniapp.webApp.SafeAreaInset
import com.kirillNay.telegram.miniapp.webApp.WebApp
import com.kirillNay.telegram.miniapp.webApp.WebAppEvent
import com.kirillNay.telegram.miniapp.webApp.internal.consoleLog
import com.kirillNay.telegram.miniapp.webApp.webApp

/**
 * Entry point of a Compose Mini App. Renders [content] into the page body and passes it the current [TelegramStyle],
 * which is also available as [LocalTelegramStyle].
 *
 * telegram-web-app.js must be loaded before calling this function. If the page can also be opened outside Telegram,
 * pass [fallback]: it is rendered instead of [content] when [WebApp.isRunningInTelegram] is false.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun telegramWebApp(
    fallback: (@Composable () -> Unit)? = null,
    content: @Composable (TelegramStyle) -> Unit,
) {
    val startTimestampMillis = performanceNow()

    if (fallback != null && !WebApp.isRunningInTelegram) {
        runWhenComposeReady { ComposeViewport { fallback() } }
        return
    }

    runWhenComposeReady {
        ComposeViewport {
            val timeToFirstFrameLogger = remember { TimeToFirstFrameLogger(startTimestampMillis) }
            SideEffect {
                timeToFirstFrameLogger.onFirstComposeCommit()
            }

            TelegramWebAppContent(webApp, content)
        }
    }
}

/** Keeps [TelegramStyle] in sync with [webApp] events and provides it to [content]. */
@Composable
internal fun TelegramWebAppContent(
    webApp: WebApp,
    content: @Composable (TelegramStyle) -> Unit,
) {
    var style by remember(webApp) { mutableStateOf(webApp.currentStyle()) }

    DisposableEffect(webApp) {
        val update: (Any?) -> Unit = { style = webApp.currentStyle() }
        val subscriptions = listOf(
            webApp.onEvent(WebAppEvent.ViewportChanged, update),
            webApp.onEvent(WebAppEvent.ThemeChanged, update),
            webApp.onEvent(WebAppEvent.SafeAreaChanged, update),
            webApp.onEvent(WebAppEvent.ContentSafeAreaChanged, update),
        )
        onDispose { subscriptions.forEach { it.unsubscribe() } }
    }

    CompositionLocalProvider(LocalTelegramStyle provides style) {
        content(style)
    }
}

internal fun WebApp.currentStyle() = TelegramStyle(
    viewPort = ViewPort(viewportHeight.dp, viewportStableHeight.dp),
    colors = TelegramColors.from(themeParams),
    colorScheme = colorScheme,
    safeAreaInset = safeAreaInset.toPaddingValues(),
    contentSafeAreaInset = contentSafeAreaInset.toPaddingValues(),
)

private fun SafeAreaInset.toPaddingValues() = PaddingValues(
    start = left.dp,
    top = top.dp,
    end = right.dp,
    bottom = bottom.dp,
)

/** Runs [block] once the Compose runtime can render: the js target has to wait for the Skiko Wasm module. */
internal expect fun runWhenComposeReady(block: () -> Unit)

private fun performanceNow(): Double = js("performance.now()")

private fun requestAnimationFrame(callback: () -> Unit) {
    js("requestAnimationFrame(function () { callback(); });")
}

private class TimeToFirstFrameLogger(
    private val startTimestampMillis: Double,
) {
    private var isScheduled = false

    fun onFirstComposeCommit() {
        if (isScheduled) return
        isScheduled = true

        // Two RAF hops ensure the log runs after the first browser paint.
        requestAnimationFrame {
            requestAnimationFrame {
                val timeToFirstFrameMillis = performanceNow() - startTimestampMillis
                consoleLog("[tg-mini-app] Time To First Frame: ${timeToFirstFrameMillis.toInt()} ms")
            }
        }
    }
}
