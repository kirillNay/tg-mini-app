package com.kirillNay.telegram.miniapp.webApp

/**
 * Chat types that can be offered to the user in [WebApp.switchInlineQuery].
 */
enum class ChatType(val value: String) {

    USERS("users"),

    BOTS("bots"),

    GROUPS("groups"),

    CHANNELS("channels")
}
