package com.kirillnay.tgminiapp.samples.coffee

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.kirillNay.telegram.miniapp.compose.telegramWebApp
import com.kirillNay.telegram.miniapp.webApp.WebApp
import com.kirillNay.telegram.miniapp.webApp.webApp

fun main() {
    val isTelegramRuntime = WebApp.isRunningInTelegram
    println(
        "[coffee-order-demo] mode=${if (isTelegramRuntime) "telegram-mini-app" else "browser-placeholder"}, " +
            "telegram-web-app.js loaded=${WebApp.isAvailable}"
    )

    if (isTelegramRuntime) {
        webApp.ready()
        webApp.expand()
        webApp.enableClosingConfirmation()
        webApp.setBackgroundColor("bg_color")
        webApp.setHeaderColor("secondary_bg_color")
    }

    telegramWebApp(fallback = { TelegramRuntimePlaceholder() }) { style ->
        val bridge = remember(style) { TelegramPlatformBridge(style) }
        CoffeeOrderDemoApp(
            bridge = bridge,
            modifier = Modifier.padding(style.contentSafeAreaInset),
        )
    }
}
