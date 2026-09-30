package com.kirillNay.telegram.miniapp.compose

import androidx.compose.ui.graphics.Color
import com.kirillNay.telegram.miniapp.webApp.ThemeParams

/**
 * Telegram theme colors mapped to Compose [Color]. Colors missing in older Telegram clients fall back to related colors.
 */
data class TelegramColors(
    val backgroundColor: Color,
    val textColor: Color,
    val hintColor: Color,
    val linkColor: Color,
    val buttonColor: Color,
    val buttonTextColor: Color,
    val secondaryBackgroundColor: Color,
    /** Bot API 7.0+ */
    val headerBackgroundColor: Color,
    /** Bot API 7.10+ */
    val bottomBarBackgroundColor: Color,
    /** Bot API 7.0+ */
    val accentTextColor: Color,
    /** Bot API 7.0+ */
    val sectionBackgroundColor: Color,
    /** Bot API 7.0+ */
    val sectionHeaderTextColor: Color,
    /** Bot API 7.6+ */
    val sectionSeparatorColor: Color,
    /** Bot API 7.0+ */
    val subtitleTextColor: Color,
    /** Bot API 7.0+ */
    val destructiveTextColor: Color,
) {

    companion object {

        fun from(themeParams: ThemeParams): TelegramColors = themeParams.run {
            val background = bgColor.toColor() ?: Color.White
            val secondaryBackground = secondaryBgColor.toColor() ?: Color(0xFFEFEFF4)
            val link = linkColor.toColor() ?: Color(0xFF2481CC)
            val hint = hintColor.toColor() ?: Color(0xFF999999)
            val accent = accentTextColor.toColor() ?: link
            TelegramColors(
                backgroundColor = background,
                textColor = textColor.toColor() ?: Color.Black,
                hintColor = hint,
                linkColor = link,
                buttonColor = buttonColor.toColor() ?: Color(0xFF2481CC),
                buttonTextColor = buttonTextColor.toColor() ?: Color.White,
                secondaryBackgroundColor = secondaryBackground,
                headerBackgroundColor = headerBgColor.toColor() ?: background,
                bottomBarBackgroundColor = bottomBarBgColor.toColor() ?: secondaryBackground,
                accentTextColor = accent,
                sectionBackgroundColor = sectionBgColor.toColor() ?: background,
                sectionHeaderTextColor = sectionHeaderTextColor.toColor() ?: accent,
                sectionSeparatorColor = sectionSeparatorColor.toColor() ?: secondaryBackground,
                subtitleTextColor = subtitleTextColor.toColor() ?: hint,
                destructiveTextColor = destructiveTextColor.toColor() ?: Color(0xFFE53935),
            )
        }

        /** Parses `#RRGGBB` or `#RRGGBBAA`. */
        internal fun String?.toColor(): Color? {
            val hex = this?.removePrefix("#") ?: return null
            val value = hex.toLongOrNull(16) ?: return null
            return when (hex.length) {
                6 -> Color(0xFF000000 or value)
                8 -> Color(((value and 0xFF) shl 24) or (value ushr 8))
                else -> null
            }
        }
    }
}
