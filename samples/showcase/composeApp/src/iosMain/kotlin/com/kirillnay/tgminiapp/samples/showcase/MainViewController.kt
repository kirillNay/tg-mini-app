package com.kirillnay.tgminiapp.samples.showcase

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    val isDark = isSystemInDarkTheme()
    val platform = remember(isDark) { OutsideTelegramPlatform(hostName = "iOS", isDark = isDark) }
    Box(Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
        ShowcaseApp(platform)
    }
}
