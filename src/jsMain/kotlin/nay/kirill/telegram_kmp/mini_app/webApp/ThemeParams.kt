package nay.kirill.telegram_kmp.mini_app.webApp

/**
 * Mini Apps can [adjust the appearance](https://core.telegram.org/bots/webapps#color-schemes) of the interface to match the Telegram user's app in real time.
 * This object contains the user's current theme settings.
 */
external class ThemeParams {

    /**
     * Optional. Background color in the #RRGGBB format.
     * Also available as the CSS variable var(--tg-theme-bg-color).
     */
    @JsName("bg_color")
    val bgColor: String?

    /**
     * Optional. Main text color in the #RRGGBB format.
     * Also available as the CSS variable var(--tg-theme-text-color).
     */
    @JsName("text_color")
    val textColor: String?

    /**
     * Optional. Hint text color in the #RRGGBB format.
     * Also available as the CSS variable var(--tg-theme-hint-color).
     */
    @JsName("hint_color")
    val hintColor: String?

    /**
     * Optional. Link color in the #RRGGBB format.
     * Also available as the CSS variable var(--tg-theme-link-color).
     */
    @JsName("link_color")
    val linkColor: String?

    /**
     * Optional. Button color in the #RRGGBB format.
     * Also available as the CSS variable var(--tg-theme-button-color).
     */
    @JsName("button_color")
    val buttonColor: String?

    /**
     * Optional. Button text color in the #RRGGBB format.
     * Also available as the CSS variable var(--tg-theme-button-text-color).
     */
    @JsName("button_text_color")
    val buttonTextColor: String?

    /**
     * Optional. Bot API 6.1+ Secondary background color in the #RRGGBB format.
     * Also available as the CSS variable var(--tg-theme-secondary-bg-color).
     */
    @JsName("secondary_bg_color")
    val secondaryBgColor: String?

}