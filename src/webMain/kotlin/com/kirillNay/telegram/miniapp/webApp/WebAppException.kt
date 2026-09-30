package com.kirillNay.telegram.miniapp.webApp

/**
 * An error reported by telegram-web-app.js, for example by one of the storages.
 * [message] contains the error string passed by Telegram.
 */
class WebAppException(message: String) : Exception(message)
