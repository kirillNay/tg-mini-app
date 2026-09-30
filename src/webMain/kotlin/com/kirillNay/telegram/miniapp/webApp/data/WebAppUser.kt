package com.kirillNay.telegram.miniapp.webApp.data

import com.kirillNay.telegram.miniapp.webApp.internal.UserJs
import com.kirillNay.telegram.miniapp.webApp.internal.isTrue
import com.kirillNay.telegram.miniapp.webApp.internal.asLongOrNull

/**
 * Data of the Mini App user.
 */
data class WebAppUser(
    /** A unique identifier for the user or bot. It has at most 52 significant bits. */
    val id: Long,
    /** True, if this user is a bot. Returned in the receiver field only. */
    val isBot: Boolean,
    /** First name of the user or bot. */
    val firstName: String,
    /** Last name of the user or bot. */
    val lastName: String?,
    /** Username of the user or bot. */
    val username: String?,
    /** IETF language tag of the user's language. Returned in the user field only. */
    val languageCode: String?,
    /** True, if this user is a Telegram Premium user. */
    val isPremium: Boolean,
    /** True, if this user added the bot to the attachment menu. */
    val addedToAttachmentMenu: Boolean,
    /** True, if this user allowed the bot to message them. */
    val allowsWriteToPm: Boolean,
    /** URL of the user's profile photo. The photo can be in .jpeg or .svg formats. */
    val photoUrl: String?,
) {

    internal companion object {

        fun from(js: UserJs): WebAppUser = WebAppUser(
            id = js.id.asLongOrNull() ?: 0L,
            isBot = js.is_bot.isTrue(),
            firstName = js.first_name.orEmpty(),
            lastName = js.last_name,
            username = js.username,
            languageCode = js.language_code,
            isPremium = js.is_premium.isTrue(),
            addedToAttachmentMenu = js.added_to_attachment_menu.isTrue(),
            allowsWriteToPm = js.allows_write_to_pm.isTrue(),
            photoUrl = js.photo_url,
        )
    }
}
