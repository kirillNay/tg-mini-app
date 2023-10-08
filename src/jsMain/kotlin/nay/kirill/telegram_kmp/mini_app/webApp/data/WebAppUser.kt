package nay.kirill.telegram_kmp.mini_app.webApp.data

/**
 * This object contains the data of the Mini App user.
 *
 * https://core.telegram.org/bots/webapps#webappuser
 */
external class WebAppUser {

    /**
     * A unique identifier for the user or bot. This number may have more than 32 significant bits and some programming languages may have difficulty/silent defects in interpreting it.
     * It has at most 52 significant bits, so a 64-bit integer or a double-precision float type is safe for storing this identifier.
     */
    @JsName("id")
    val id: Int

    /**
     * Optional. True, if this user is a bot. Returns in the [WebAppInitData.receiver] field only.
     */
    @JsName("is_bot")
    val isBot: Boolean?

    /**
     * First name of the user or bot.
     */
    @JsName("first_name")
    val firstName: String

    /**
     * Optional. Last name of the user or bot.
     */
    @JsName("last_name")
    val lastName: String?

    /**
     * Optional. Username of the user or bot.
     */
    @JsName("username")
    val username: String?

    /**
     * Optional. [IETF language tag](https://en.wikipedia.org/wiki/IETF_language_tag) of the user's language. Returns in user field only.
     */
    @JsName("language_code")
    val languageCode: String?

    /**
     * Optional. True, if this user is a Telegram Premium user.
     */
    @JsName("is_premium")
    val isPremium: Boolean?

    /**
     * Optional. True, if this user added the bot to the attachment menu.
     */
    @JsName("added_to_attachment_menu")
    val addedToAttachmentMenu: Boolean?

    /**
     * Optional. True, if this user allowed the bot to message them.
     */
    @JsName("allows_write_to_pm")
    val allowsWriteToPm: Boolean?

    /**
     * Optional. URL of the user’s profile photo. The photo can be in .jpeg or .svg formats. Only returned for Mini Apps launched from the attachment menu.
     */
    @JsName("photo_url")
    val photoUrl: String?

}
