package com.kirillNay.telegram.miniapp.webApp.buttons

/**
 * The params parameter is an object containing one or several fields that need to be changed
 *
 * [text] - button text;
 *
 * [color] - button color;
 *
 * [textColor] - button text color;
 *
 * [isActive] - enable the button;
 *
 * [isVisible] - show the button.
 */
class ButtonParams(

    @JsName("text")
    val text: String? = undefined,

    @JsName("color")
    val color: String? = undefined,

    @JsName("text_color")
    val textColor: String? = undefined,

    @JsName("is_active")
    val isActive: Boolean? = undefined,

    @JsName("is_visible")
    val isVisible: Boolean? = undefined
)