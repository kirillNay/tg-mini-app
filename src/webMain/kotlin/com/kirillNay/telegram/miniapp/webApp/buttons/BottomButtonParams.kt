package com.kirillNay.telegram.miniapp.webApp.buttons

import com.kirillNay.telegram.miniapp.webApp.internal.jsObject

/**
 * Parameters for [BottomButton.setParams]. Fields left `null` are not changed.
 *
 * @param text button text.
 * @param color button color in the #RRGGBB format.
 * @param textColor button text color in the #RRGGBB format.
 * @param hasShineEffect Bot API 7.10+ enables the shine effect.
 * @param position Bot API 7.10+ position of the secondary button.
 * @param isActive enables the button.
 * @param isVisible shows the button.
 * @param iconCustomEmojiId Bot API 9.5+ custom emoji shown before the button text.
 */
data class BottomButtonParams(
    val text: String? = null,
    val color: String? = null,
    val textColor: String? = null,
    val hasShineEffect: Boolean? = null,
    val position: BottomButtonPosition? = null,
    val isActive: Boolean? = null,
    val isVisible: Boolean? = null,
    val iconCustomEmojiId: String? = null,
) {

    internal fun toJs(): JsAny = jsObject {
        put("text", text)
        put("color", color)
        put("text_color", textColor)
        put("has_shine_effect", hasShineEffect)
        put("position", position?.value)
        put("is_active", isActive)
        put("is_visible", isVisible)
        put("icon_custom_emoji_id", iconCustomEmojiId)
    }
}
