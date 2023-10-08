package nay.kirill.telegram_kmp.mini_app.webApp.buttons

/**
 * This object controls the back button, which can be displayed in the header of the Mini App in the Telegram interface.
 *
 * https://core.telegram.org/bots/webapps#backbutton
 */
external class BackButton {

    /**
     * Shows whether the button is visible. Set to false by default.
     */
    @JsName("isVisible")
    val isVisible: Boolean

    /**
     * Bot API 6.1+ A method that sets the button press event handler. An alias for Telegram.WebApp.onEvent('backButtonClicked', callback)
     */
    @JsName("onClick")
    fun onClick(callback: () -> Unit): BackButton

    /**
     * Bot API 6.1+ A method that removes the button press event handler. An alias for Telegram.WebApp.offEvent('backButtonClicked', callback)
     */
    @JsName("offClick")
    fun offClick(callback: () -> Unit): BackButton

    /**
     * Bot API 6.1+ A method to make the button active and visible.
     */
    @JsName("show")
    fun show()

    /**
     * Bot API 6.1+ A method to hide the button.
     */
    @JsName("hide")
    fun hide()

}