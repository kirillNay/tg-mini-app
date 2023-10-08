package com.kirillNay.telegram.miniapp.webApp.data

/**
 * This object represents a chat.
 *
 * https://core.telegram.org/bots/webapps#webappchat
 */
external class WebAppChat {

    /**
     * Unique identifier for this chat. This number may have more than 32 significant bits and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a signed 64-bit integer or double-precision float type are safe for storing this identifier.
     */
    @JsName("id")
    val id: Int

    /**
     * Type of chat, can be either “group”, “supergroup” or “channel”
     */
    @JsName("type")
    val type: String

    /**
     * Title of the chat
     */
    @JsName("title")
    val title: String

    /**
     * Optional. Username of the chat
     */
    @JsName("username")
    val userName: String?

    /**
     * Optional. URL of the chat’s photo. The photo can be in .jpeg or .svg formats. Only returned for Mini Apps launched from the attachment menu.
     */
    @JsName("photo_url")
    val photoUrl: String?

}
