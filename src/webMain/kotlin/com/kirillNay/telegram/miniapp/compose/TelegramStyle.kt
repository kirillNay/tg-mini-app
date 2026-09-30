package com.kirillNay.telegram.miniapp.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.staticCompositionLocalOf
import com.kirillNay.telegram.miniapp.webApp.ColorScheme

/**
 * Current Telegram appearance passed to the content of [telegramWebApp]. It is updated when Telegram reports
 * theme, viewport or safe area changes.
 *
 * @param safeAreaInset Bot API 8.0+ insets that avoid system UI such as notches or navigation bars.
 * @param contentSafeAreaInset Bot API 8.0+ insets that avoid Telegram UI elements, e.g. in fullscreen mode.
 */
data class TelegramStyle(
    val viewPort: ViewPort,
    val colors: TelegramColors,
    val colorScheme: ColorScheme,
    val safeAreaInset: PaddingValues,
    val contentSafeAreaInset: PaddingValues,
) {

    val isDark: Boolean get() = colorScheme == ColorScheme.DARK
}

/** The [TelegramStyle] provided by [telegramWebApp] to the whole composition. */
val LocalTelegramStyle = staticCompositionLocalOf<TelegramStyle> {
    error("LocalTelegramStyle is provided only inside telegramWebApp { }")
}
