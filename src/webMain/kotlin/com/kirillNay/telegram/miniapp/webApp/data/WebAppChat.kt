package com.kirillNay.telegram.miniapp.webApp.data

import com.kirillNay.telegram.miniapp.webApp.internal.ChatJs
import com.kirillNay.telegram.miniapp.webApp.internal.asLongOrNull

/**
 * A chat the Mini App was launched from.
 */
data class WebAppChat(
    /** Unique identifier for this chat. It has at most 52 significant bits. */
    val id: Long,
    /** Type of chat, can be either "group", "supergroup" or "channel". */
    val type: String,
    /** Title of the chat. */
    val title: String,
    /** Username of the chat. */
    val username: String?,
    /** URL of the chat's photo. Only returned for Mini Apps launched from the attachment menu. */
    val photoUrl: String?,
) {

    internal companion object {

        fun from(js: ChatJs): WebAppChat = WebAppChat(
            id = js.id.asLongOrNull() ?: 0L,
            type = js.type.orEmpty(),
            title = js.title.orEmpty(),
            username = js.username,
            photoUrl = js.photo_url,
        )
    }
}
