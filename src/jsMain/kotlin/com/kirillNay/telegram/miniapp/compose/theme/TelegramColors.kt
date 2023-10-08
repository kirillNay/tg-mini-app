package com.kirillNay.telegram.miniapp.compose.theme

import androidx.compose.ui.graphics.Color
import com.kirillNay.telegram.miniapp.webApp.webApp

@OptIn(ExperimentalStdlibApi::class)
data class TelegramColors(
    val backgroundColor: Color,
    val textColor: Color,
    val hintColor: Color,
    val linkColor: Color,
    val buttonColor: Color,
    val buttonTextColor: Color,
    val secondaryBackgroundColor: Color
) {

    companion object {

        fun fromWebApp(): TelegramColors = webApp.themeParams.run {
            TelegramColors(
                backgroundColor = bgColor.toColor(Color.White),
                textColor = textColor.toColor(Color.Black),
                hintColor = hintColor.toColor(Color.Gray),
                linkColor = linkColor.toColor(Color.Blue),
                buttonColor = buttonColor.toColor(Color(0xFF229ED9)),
                buttonTextColor = buttonTextColor.toColor(Color.White),
                secondaryBackgroundColor = secondaryBgColor.toColor(Color.White)
            )
        }

        private fun String?.toColor(defaultColor: Color): Color =
            this?.run {
                runCatching { Color(("ff" + removePrefix("#")).hexToLong()) }.getOrNull()
            }
                ?: defaultColor

    }

}