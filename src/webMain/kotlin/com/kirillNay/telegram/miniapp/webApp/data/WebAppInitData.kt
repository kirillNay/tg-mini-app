package com.kirillNay.telegram.miniapp.webApp.data

import com.kirillNay.telegram.miniapp.webApp.internal.InitDataJs
import com.kirillNay.telegram.miniapp.webApp.internal.asIntOrNull
import com.kirillNay.telegram.miniapp.webApp.internal.asLongOrNull

/**
 * Data that is transferred to the Mini App when it is opened. It is empty if the Mini App was launched from a keyboard button or from inline mode.
 *
 * **WARNING**: this data should not be trusted. Validate [com.kirillNay.telegram.miniapp.webApp.WebApp.rawInitData] on the bot's server instead.
 */
data class WebAppInitData(
    /** A unique identifier for the Mini App session, required for sending messages via the answerWebAppQuery method. */
    val queryId: String?,
    /** Bot API 10.1+ A unique identifier for the chat join request query, required for the answerChatJoinRequestQuery method. */
    val chatJoinRequestQueryId: String?,
    /** Data about the current user. */
    val user: WebAppUser?,
    /** Data about the chat partner of the current user. Returned only for private chats and only for Mini Apps launched via the attachment menu. */
    val receiver: WebAppUser?,
    /** Data about the chat where the bot was launched via the attachment menu, or the chat to which the join request query was sent. */
    val chat: WebAppChat?,
    /** Type of the chat from which the Mini App was opened: "sender", "private", "group", "supergroup" or "channel". Returned only for direct links. */
    val chatType: String?,
    /** Global identifier, uniquely corresponding to the chat from which the Mini App was opened. Returned only for direct links. */
    val chatInstance: String?,
    /** The value of the startattach or startapp parameter, passed via link. */
    val startParam: String?,
    /** Time in seconds, after which a message can be sent via the answerWebAppQuery method. */
    val canSendAfter: Int?,
    /** Unix time in seconds when the form was opened. */
    val authDate: Long?,
    /** A hash of all passed parameters, which the bot server can use to check their validity. */
    val hash: String?,
    /** Bot API 8.0+ A signature of all passed parameters (except hash), which a third party can use to check their validity. */
    val signature: String?,
) {

    internal companion object {

        fun from(js: InitDataJs): WebAppInitData = WebAppInitData(
            queryId = js.query_id,
            chatJoinRequestQueryId = js.chat_join_request_query_id,
            user = js.user?.let(WebAppUser::from),
            receiver = js.receiver?.let(WebAppUser::from),
            chat = js.chat?.let(WebAppChat::from),
            chatType = js.chat_type,
            chatInstance = js.chat_instance,
            startParam = js.start_param,
            // telegram-web-app.js keeps these as strings parsed from the query string.
            canSendAfter = js.can_send_after.asIntOrNull(),
            authDate = js.auth_date.asLongOrNull(),
            hash = js.hash,
            signature = js.signature,
        )
    }
}
