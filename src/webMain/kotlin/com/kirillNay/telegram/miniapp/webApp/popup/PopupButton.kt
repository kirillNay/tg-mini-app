package com.kirillNay.telegram.miniapp.webApp.popup

import com.kirillNay.telegram.miniapp.webApp.internal.jsObject

/**
 * A native popup button.
 *
 * @param id identifier of the button, 0-64 characters. It is returned when the button is pressed. Empty by default.
 * @param type type of the button.
 * @param text the text to be displayed on the button, 0-64 characters. Required for [Type.DEFAULT] and [Type.DESTRUCTIVE], irrelevant for other types.
 */
data class PopupButton(
    val id: String? = null,
    val type: Type = Type.DEFAULT,
    val text: String? = null,
) {

    enum class Type(val value: String) {
        /** A button with the default style. */
        DEFAULT("default"),

        /** A button with the localized text "OK". */
        OK("ok"),

        /** A button with the localized text "Close". */
        CLOSE("close"),

        /** A button with the localized text "Cancel". */
        CANCEL("cancel"),

        /** A button with a style that indicates a destructive action (e.g. "Remove", "Delete"). */
        DESTRUCTIVE("destructive")
    }

    internal fun toJs(): JsAny = jsObject {
        put("id", id)
        put("type", type.value)
        put("text", text)
    }
}
