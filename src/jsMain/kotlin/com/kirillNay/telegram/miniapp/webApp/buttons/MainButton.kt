package com.kirillNay.telegram.miniapp.webApp.buttons

/**
 * This object controls the main button, which is displayed at the bottom of the Mini App in the Telegram interface.
 */
external class MainButton {

    /**
     * Current button text. Set to CONTINUE by default.
     */
    @JsName("text")
    val text: String

    /**
     * Current button color. Set to themeParams.button_color by default.
     */
    @JsName("color")
    val color: String

    /**
     * Current button text color. Set to themeParams.button_text_color by default.
     */
    @JsName("textColor")
    val textColor: String

    /**
     * Shows whether the button is visible. Set to false by default.
     */
    @JsName("isVisible")
    val isVisible: Boolean

    /**
     * Shows whether the button is active. Set to true by default.
     */
    @JsName("isActive")
    val isActive: Boolean

    /**
     * Readonly. Shows whether the button is displaying a loading indicator.
     */
    @JsName("isProgressVisible")
    val isProgressVisible: Boolean

    /**
     * A method to set the button text.
     */
    @JsName("setText")
    fun setText(text: String): MainButton

    /**
     * A method that sets the button press event handler. An alias for Telegram.WebApp.onEvent('mainButtonClicked', callback)
     */
    @JsName("onClick")
    fun onClick(callback: () -> Unit): MainButton

    /**
     * A method that removes the button press event handler. An alias for Telegram.WebApp.offEvent('mainButtonClicked', callback)
     */
    @JsName("offClick")
    fun offClick(callback: () -> Unit): MainButton

    /**
     * A method to make the button visible.
     * Note that opening the Mini App from the [attachment menu](https://core.telegram.org/bots/webapps#launching-mini-apps-from-the-attachment-menu) hides the main button until the user interacts with the Mini App interface.
     */
    @JsName("show")
    fun show(): MainButton

    /**
     * A method to hide the button.
     */
    @JsName("hide")
    fun hide(): MainButton

    /**
     * A method to enable the button.
     */
    @JsName("enable")
    fun enable(): MainButton

    /**
     * A method to disable the button.
     */
    @JsName("disable")
    fun disable(): MainButton

    /**
     * A method to show a loading indicator on the button.
     * It is recommended to display loading progress if the action tied to the button may take a long time. By default, the button is disabled while the action is in progress. If the parameter [leaveActive]=true is passed, the button remains enabled.
     */
    @JsName("showProgress")
    fun showProgress(leaveActive: Boolean): MainButton

    /**
     * A method to hide the loading indicator.
     */
    @JsName("hideProgress")
    fun hideProgress(): MainButton

    /**
     * A method to set the button parameters.
     */
    @JsName("setParams")
    fun setParams(params: ButtonParams): MainButton

}