package com.kirillnay.tgminiapp.samples.coffee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.remember

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDark = isSystemInDarkTheme()
            val bridge = remember(isDark) {
                DemoPlatformBridge(
                    environment = demoEnvironment(
                        platformName = "Android",
                        isDark = isDark,
                    ),
                )
            }
            CoffeeOrderDemoApp(bridge = bridge)
        }
    }
}
