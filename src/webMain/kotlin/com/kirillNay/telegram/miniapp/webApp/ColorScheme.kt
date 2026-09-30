package com.kirillNay.telegram.miniapp.webApp

/**
 * The color scheme currently used in the Telegram app.
 */
enum class ColorScheme(val value: String) {

    LIGHT("light"),

    DARK("dark");

    internal companion object {

        /** Telegram reports only "light" or "dark"; anything else falls back to [LIGHT]. */
        fun from(value: String?): ColorScheme = entries.find { it.value == value } ?: LIGHT
    }
}
