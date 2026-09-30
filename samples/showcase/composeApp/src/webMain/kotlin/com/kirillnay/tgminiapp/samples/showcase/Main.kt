package com.kirillnay.tgminiapp.samples.showcase

import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import com.kirillNay.telegram.miniapp.compose.telegramWebApp
import com.kirillNay.telegram.miniapp.webApp.WebApp
import com.kirillNay.telegram.miniapp.webApp.webApp

fun main() {
    val isTelegramRuntime = WebApp.isRunningInTelegram
    println("[showcase] mode=${if (isTelegramRuntime) "telegram-mini-app" else "browser"}, telegram-web-app.js loaded=${WebApp.isAvailable}")

    if (isTelegramRuntime) {
        webApp.ready()
        webApp.expand()
    }

    telegramWebApp(
        fallback = {
            // Opened in a regular browser: the catalog is shown, every feature explains that it needs Telegram.
            ShowcaseApp(remember { OutsideTelegramPlatform(hostName = "Web browser", isDark = false) })
        },
    ) { style ->
        val platform = remember { TelegramShowcasePlatform(webApp).also { it.update(style) } }
        SideEffect { platform.update(style) }
        ShowcaseApp(platform)
    }
}
