package com.kirillnay.tgminiapp.samples.coffee

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    val isDark = isSystemInDarkTheme()
    val bridge = remember(isDark) {
        DemoPlatformBridge(
            environment = demoEnvironment(
                platformName = "iOS",
                isDark = isDark,
            ),
        )
    }
    CoffeeOrderDemoApp(bridge = bridge)
}
