package nay.kirill.telegram_kmp.mini_app.compose.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Stable
import nay.kirill.telegram_kmp.mini_app.webApp.ColorScheme

/**
 * Use for providing mini app colors based on current [TelegramColors] and [ColorScheme].
 */
interface ColorsConverter {

    /**
     * @param themeParams contains colors of the telegram app theme.
     *
     * @param colorScheme contains current theme mode: light or dark of the telegram app.
     */
    fun convert(themeParams: TelegramColors, colorScheme: ColorScheme): Colors

    @Stable
    class Default : ColorsConverter {

        override fun convert(themeParams: TelegramColors, colorScheme: ColorScheme): Colors {
            console.log(colorScheme.value)

            return when (colorScheme) {
                ColorScheme.LIGHT -> lightColors()
                ColorScheme.DARK -> darkColors()
            }
        }

    }

}

