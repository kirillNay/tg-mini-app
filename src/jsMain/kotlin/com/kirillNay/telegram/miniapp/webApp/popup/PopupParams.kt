package com.kirillNay.telegram.miniapp.webApp.popup

/**
 * This object describes the native popup.
 *
 * @param title
 * Optional. The text to be displayed in the popup title, 0-64 characters.
 *
 * @param message
 * The message to be displayed in the body of the popup, 1-256 characters.
 *
 * @param buttons
 * Optional. List of buttons to be displayed in the popup, 1-3 buttons. Set to [{“type”:“close”}] by default.
 */
class PopupParams(
    @JsName("title")
    val title: String? = null,

    @JsName("message")
    val message: String,

    @JsName("buttons")
    val buttons: Array<PopupButton>
)