package com.kirillNay.telegram.miniapp.webApp

import com.kirillNay.telegram.miniapp.webApp.internal.jsObject

/**
 * Bot API 8.0+
 *
 * Additional settings for [WebApp.setEmojiStatus].
 *
 * @param duration the duration for which the status will remain set, in seconds.
 */
data class EmojiStatusParams(
    val duration: Int? = null,
) {

    internal fun toJs(): JsAny = jsObject { put("duration", duration) }
}
