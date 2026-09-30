package com.kirillNay.telegram.miniapp.webApp.popup

import com.kirillNay.telegram.miniapp.webApp.internal.jsObject

/**
 * A native popup shown with [com.kirillNay.telegram.miniapp.webApp.WebApp.showPopup].
 *
 * @param message the message to be displayed in the body of the popup, 1-256 characters.
 * @param title the text to be displayed in the popup title, 0-64 characters.
 * @param buttons 1-3 buttons to be displayed in the popup. A single "Close" button is shown when the list is empty.
 */
data class PopupParams(
    val message: String,
    val title: String? = null,
    val buttons: List<PopupButton> = emptyList(),
) {

    internal fun toJs(): JsAny = jsObject {
        put("title", title)
        put("message", message)
        if (buttons.isNotEmpty()) {
            put("buttons", buttons.map { it.toJs() }.toJsArray())
        }
    }
}
