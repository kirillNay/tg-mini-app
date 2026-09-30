package com.kirillNay.telegram.miniapp.webApp

import com.kirillNay.telegram.miniapp.webApp.internal.ThemeParamsJs

/**
 * The user's current Telegram theme. All colors are in the #RRGGBB format and may be missing.
 */
data class ThemeParams(
    /** Background color. */
    val bgColor: String?,
    /** Main text color. */
    val textColor: String?,
    /** Hint text color. */
    val hintColor: String?,
    /** Link color. */
    val linkColor: String?,
    /** Button color. */
    val buttonColor: String?,
    /** Button text color. */
    val buttonTextColor: String?,
    /** Bot API 6.1+ Secondary background color. */
    val secondaryBgColor: String?,
    /** Bot API 7.0+ Header background color. */
    val headerBgColor: String?,
    /** Bot API 7.10+ Bottom background color. */
    val bottomBarBgColor: String?,
    /** Bot API 7.0+ Accent text color. */
    val accentTextColor: String?,
    /** Bot API 7.0+ Background color for the section. It is recommended to use this in conjunction with [secondaryBgColor]. */
    val sectionBgColor: String?,
    /** Bot API 7.0+ Header text color for the section. */
    val sectionHeaderTextColor: String?,
    /** Bot API 7.6+ Section separator color. */
    val sectionSeparatorColor: String?,
    /** Bot API 7.0+ Subtitle text color. */
    val subtitleTextColor: String?,
    /** Bot API 7.0+ Text color for destructive actions. */
    val destructiveTextColor: String?,
) {

    internal companion object {

        fun from(js: ThemeParamsJs): ThemeParams = ThemeParams(
            bgColor = js.bg_color,
            textColor = js.text_color,
            hintColor = js.hint_color,
            linkColor = js.link_color,
            buttonColor = js.button_color,
            buttonTextColor = js.button_text_color,
            secondaryBgColor = js.secondary_bg_color,
            headerBgColor = js.header_bg_color,
            bottomBarBgColor = js.bottom_bar_bg_color,
            accentTextColor = js.accent_text_color,
            sectionBgColor = js.section_bg_color,
            sectionHeaderTextColor = js.section_header_text_color,
            sectionSeparatorColor = js.section_separator_color,
            subtitleTextColor = js.subtitle_text_color,
            destructiveTextColor = js.destructive_text_color,
        )
    }
}
