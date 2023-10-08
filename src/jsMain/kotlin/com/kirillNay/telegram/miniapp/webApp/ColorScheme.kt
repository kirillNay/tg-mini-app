package com.kirillNay.telegram.miniapp.webApp

enum class ColorScheme(val value: String) {

    LIGHT("light"), DARK("dark");

    companion object {

        fun getValue(value: String): ColorScheme = ColorScheme.values().find { it.value == value }
            ?: error("Unknown colorScheme")
    }

}