package com.kirillNay.telegram.miniapp.webApp.data

/**
 * This object contains data that is transferred to the Mini App when it is opened. It is empty if the Mini App was launched from a keyboard button or from inline mode.
 *
 * https://core.telegram.org/bots/webapps#webappinitdata
 */
external class WebAppInitData {

    /**
     * Optional. A unique identifier for the Mini App session, required for sending messages via the [answerWebAppQuery](https://core.telegram.org/bots/api#answerwebappquery) method.
     */
    @JsName("query_id")
    val queryId: String?

    /**
     * Optional. An object containing data about the current user.
     */
    @JsName("user")
    val user: WebAppUser?

    /**
     * Optional. An object containing data about the chat partner of the current user in the chat where the bot was launched via the attachment menu. Returned only for private chats and only for Mini Apps launched via the attachment menu.
     */
    @JsName("receiver")
    val receiver: WebAppUser?

    /**
     * Optional. An object containing data about the chat where the bot was launched via the attachment menu. Returned for supergroups, channels and group chats – only for Mini Apps launched via the attachment menu.
     */
    @JsName("chat")
    val chat: WebAppChat?

    /**
     * Optional. Type of the chat from which the Mini App was opened. Can be either “sender” for a private chat with the user opening the link, “private”, “group”, “supergroup”, or “channel”. Returned only for Mini Apps launched from direct links.
     */
    @JsName("chat_type")
    val chatType: String?

    /**
     * Optional. Global identifier, uniquely corresponding to the chat from which the Mini App was opened. Returned only for Mini Apps launched from a direct link.
     */
    @JsName("chat_instance")
    val chatInstance: String?

    /**
     * Optional. The value of the startattach parameter, passed [via link](https://core.telegram.org/bots/webapps#adding-bots-to-the-attachment-menu). Only returned for Mini Apps when launched from the attachment menu via link.
     *
     * The value of the start_param parameter will also be passed in the GET-parameter tgWebAppStartParam, so the Mini App can load the correct interface right away.
     */
    @JsName("start_param")
    val startParam: String?

    /**
     * Optional. Time in seconds, after which a message can be sent via the [answerWebAppQuery](https://core.telegram.org/bots/api#answerwebappquery) method.
     */
    @JsName("can_send_after")
    val canSendAfter: Int?

    /**
     * Unix time when the form was opened.
     */
    @JsName("auth_date")
    val authDate: Int

    /**
     * A hash of all passed parameters, which the bot server can use to [check their validity](https://core.telegram.org/bots/webapps#validating-data-received-via-the-mini-app).
     */
    @JsName("hash")
    val hash: String?

}
