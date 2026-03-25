package com.kirillnay.tgminiapp.samples.coffee

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.compose.runtime.remember
import com.kirillNay.telegram.miniapp.compose.telegramWebApp
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val runtimeInfo = detectRuntime()
    logRuntime(runtimeInfo)

    if (runtimeInfo.isTelegramRuntime) {
        telegramWebApp { style ->
            val bridge = remember(style.colors, style.viewPort.viewPortHeight, style.viewPort.viewportStableHeight) {
                TelegramPlatformBridge(style)
            }
            CoffeeOrderDemoApp(bridge = bridge)
        }
    } else {
        onWasmReady {
            ComposeViewport(document.body!!) {
                TelegramRuntimePlaceholder()
            }
        }
    }
}

private data class RuntimeInfo(
    val isTelegramRuntime: Boolean,
    val hasTelegramObject: Boolean,
    val hasWebAppObject: Boolean,
    val hasRawInitData: Boolean,
)

@Suppress("UnsafeCastFromDynamic")
private fun detectRuntime(): RuntimeInfo {
    val hasTelegramObject = js("typeof window !== 'undefined' && !!window.Telegram") as Boolean
    val hasWebAppObject = js("typeof window !== 'undefined' && !!window.Telegram && !!window.Telegram.WebApp") as Boolean
    val hasRawInitData = js(
        "typeof window !== 'undefined' && !!window.Telegram && !!window.Telegram.WebApp && typeof window.Telegram.WebApp.initData === 'string' && window.Telegram.WebApp.initData.length > 0"
    ) as Boolean

    return RuntimeInfo(
        isTelegramRuntime = hasRawInitData,
        hasTelegramObject = hasTelegramObject,
        hasWebAppObject = hasWebAppObject,
        hasRawInitData = hasRawInitData,
    )
}

private fun logRuntime(runtimeInfo: RuntimeInfo) {
    val payload = js("{}")
    payload.href = window.location.href
    payload.userAgent = window.navigator.userAgent
    payload.hasTelegramObject = runtimeInfo.hasTelegramObject
    payload.hasWebAppObject = runtimeInfo.hasWebAppObject
    payload.hasRawInitData = runtimeInfo.hasRawInitData
    payload.mode = if (runtimeInfo.isTelegramRuntime) "telegram-mini-app" else "browser-placeholder"

    console.log("[coffee-order-demo] runtime check", payload)

    if (runtimeInfo.isTelegramRuntime) {
        console.log("[coffee-order-demo] starting Telegram Mini App mode")
    } else {
        console.warn("[coffee-order-demo] Telegram runtime not found, showing browser placeholder")
    }
}
