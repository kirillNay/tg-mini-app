package com.kirillNay.telegram.miniapp.webApp.popup

/**
 * This object describes the native popup button.
 *
 * @param id
 * Optional. Identifier of the button, 0-64 characters. Set to empty string by default.
 * If the button is pressed, its id is returned in the callback and the popupClosed event.
 *
 * @param buttonType
 * Optional. Type of the button. Set to default by default.
 *
 * @param text
 * Optional. The text to be displayed on the button, 0-64 characters. Required if type is default or destructive. Irrelevant for other types.
 */
class PopupButton(
    @JsName("id")
    val id: String? = null,

    @JsName("text")
    val text: String? = null,

    val buttonType: Type? = null
) {

    @JsName("type")
    private val type = buttonType?.value ?: undefined

    enum class Type(val value: String) {
        DEFAULT("default"),
        OK("ok"),
        CLOSE("close"),
        CANCEL("cancel"),
        DESTRUCTIVE("destructive")
    }

}