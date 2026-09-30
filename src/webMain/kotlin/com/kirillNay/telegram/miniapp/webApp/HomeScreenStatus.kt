package com.kirillNay.telegram.miniapp.webApp

/**
 * Bot API 8.0+
 *
 * Status returned by [WebApp.checkHomeScreenStatus] and the [WebAppEvent.HomeScreenChecked] event.
 */
enum class HomeScreenStatus(val value: String) {

    /** The feature is not supported, and it is not possible to add the icon to the home screen. */
    UNSUPPORTED("unsupported"),

    /** The feature is supported, and the icon can be added, but it is not possible to determine if the icon has already been added. */
    UNKNOWN("unknown"),

    /** The icon has already been added to the home screen. */
    ADDED("added"),

    /** The icon has not been added to the home screen. */
    MISSED("missed");

    internal companion object {

        fun from(value: String?): HomeScreenStatus = entries.find { it.value == value } ?: UNKNOWN
    }
}
